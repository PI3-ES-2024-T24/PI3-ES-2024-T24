package com.puc.pi3_es_2024_t24.main

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.puc.pi3_es_2024_t24.databinding.FragmentMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.functions.FirebaseFunctions
import com.puc.pi3_es_2024_t24.utils.BitmapHelper
import com.puc.pi3_es_2024_t24.utils.MarkerInfoAdapter
import com.puc.pi3_es_2024_t24.R
import com.puc.pi3_es_2024_t24.models.MarkerData

class MapsFragment : Fragment(), OnMapReadyCallback {
    private lateinit var functions: FirebaseFunctions
    private val firebaseApp = FirebaseApp.getInstance()
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var currentLocation: Location
    private lateinit var binding: FragmentMapsBinding
    private val locations = arrayListOf<MarkerData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // pega a instancia do firebase da equipe
        functions = FirebaseFunctions.getInstance(firebaseApp, "southamerica-east1")
        //infla bindings relacionados
        binding = FragmentMapsBinding.inflate(inflater, container, false)
        //Acessa os serviços de localização da atividade relacionada à esse fragmento
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        getLocation()
        return binding.root
    }

    private fun getLocation() {
        //cria as instruçôes de permissão para o aplicativo da localização precisa ou aproximada
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    if (ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {

                        return@registerForActivityResult
                    }
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        //registra a ultima localizção do usuario
                        currentLocation=location
                        Toast.makeText(requireContext(), "Localizado",Toast.LENGTH_SHORT).show()
                        //sincroniza o mapa "map" com o fragmeento
                        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
                        mapFragment.getMapAsync(this)

                    }
                }else -> {
                // No location access granted.
            }
            }
        }
        //lança as instruções de permissão
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
    // override quando o mapa do google maps fica pronto
    override fun onMapReady(googleMap: GoogleMap) {
        val currentLoc= LatLng(currentLocation.latitude,currentLocation.longitude)
        map = googleMap
        // move a camera como posição inicial baseada na ultima localização do usuario
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc,15f))
        getUnities()
        //checa permissões de rede
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        //incia icone da localizção do usario
        map.isMyLocationEnabled = true

        //ao clicar em algum marcador...
        map.setOnMarkerClickListener { marker ->
            //seta  visibilidade do botão como visivel
            binding.navFab.visibility = View.VISIBLE
            binding.navFab.setOnClickListener {
                // chama a função navIntent tendo a posição do marcador clicada como
                navIntent(marker.position)
            }
            marker.showInfoWindow()
            true
        }
        // ao clicar no mapa
        map.setOnMapClickListener {
            binding.navFab.visibility = View.GONE
        }

    }
    // puxa do banco de dados os dados das unidades
    private fun getUnities(): Task<Unit> {
        return functions
            .getHttpsCallable("getAllUnities")
            .call()
            .continueWith { task ->
                locations.clear()
                val result = task.result?.data as? List<Map<String, Any>>

                if (result == null) {
                    Log.e(TAG, "Resposta inválida da função Firebase Functions")
                    throw IllegalStateException("Resposta inválida da função Firebase Functions")
                }

                val unities = result.map { unity ->
                    val unityId = unity["unityId"] as String
                    val gerenteCpf = unity["gerenteCpf"] as String
                    val precos = unity["precos"] as Map<String, Int>
                    val localizacao = unity["localizacao"] as Map<String, Any>

                    val nome = localizacao["nome"] as String
                    val latitude = localizacao["latitude"] as Double
                    val longitude = localizacao["longitude"] as Double
                    val endereco = localizacao["endereco"] as String
                    val referencia = localizacao["referencia"] as String

                    locations.add(
                        MarkerData(
                        unityId,
                        nome,
                        LatLng(latitude, longitude),
                        endereco,
                        gerenteCpf.toFloat(),
                        referencia
                    )
                    )
                }
                val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
                mapFragment.getMapAsync { googleMap ->
                    googleMap.setInfoWindowAdapter(MarkerInfoAdapter(requireContext()))
                    addMarkers(googleMap)
                }

            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Falha ao obter as localizações", exception)
            }
    }
    // função que inicia navegação do marker
    private fun navIntent(location: LatLng) {
        val intent = Uri.parse("google.navigation:q=${location.latitude}, ${location.longitude}&mode=w")
        val mapIntent = Intent(Intent.ACTION_VIEW, intent)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }
    // adiciona markers ao mapa
    private fun addMarkers(googleMap: GoogleMap) {
        locations.forEach { location ->
            val marker = googleMap.addMarker(
                MarkerOptions().title(location.name).snippet(location.address)
                    .position(location.latLng).icon(
                        BitmapHelper.vectorToBitmap(
                            requireContext(),
                            R.drawable.lock_icon,
                            ContextCompat.getColor(
                                requireContext(),
                                androidx.appcompat.R.color.material_blue_grey_800
                            )
                        )
                    )
            )
            marker?.tag = location
        }
    }
}

