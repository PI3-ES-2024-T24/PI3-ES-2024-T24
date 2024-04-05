package com.puc.pi3_es_2024_t24

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.puc.pi3_es_2024_t24.databinding.FragmentMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableResult
import com.google.firebase.functions.functions
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import org.json.JSONObject

class MapsFragment : Fragment(), OnMapReadyCallback {
    private lateinit var functions: FirebaseFunctions
    private val firebaseApp = FirebaseApp.getInstance()
    private lateinit var map: GoogleMap
    private lateinit var binding: FragmentMapsBinding
    private val locations = arrayListOf<MarkerData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        functions = FirebaseFunctions.getInstance(firebaseApp, "southamerica-east1")
        getUnities()
        binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

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

                    locations.add(MarkerData(
                        nome,
                        LatLng(latitude, longitude),
                        endereco,
                        gerenteCpf.toFloat(),
                        referencia
                    ))
                }
                val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
                mapFragment.getMapAsync { googleMap ->
                    googleMap.setInfoWindowAdapter(MarkerInfoAdapter(requireContext()))
                    addMarkers(googleMap)

                    googleMap.setOnMapLoadedCallback {
                        val bounds = LatLngBounds.builder()

                        locations.forEach {
                            bounds.include(it.latLng)
                        }

                        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 100))
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Falha ao obter as localizações", exception)
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "Criado")
        Log.d(TAG, "sincronizado")

    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        Log.d(TAG, "Mapa pronto")
        Log.d(TAG, "Marcador posicionado")
        val puc = LatLng(-22.83400, -47.05276)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(puc, 15f))
        Log.d(TAG, "loc inical")

    }


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

