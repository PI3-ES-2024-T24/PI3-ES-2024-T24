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
import org.json.JSONArray
import org.json.JSONObject

class MapsFragment : Fragment(), OnMapReadyCallback {
    private lateinit var functions: FirebaseFunctions
    private val firebaseApp = FirebaseApp.getInstance()

    //temporario
    private val location1 = MarkerData(
        "LOCAL 1",
        LatLng(-22.8345916, -47.0540574),
        "Av. Profa. Ana Maria Silvestre Adade, 255-395 - Parque das Universidades, Campinas - SP",
        4.9f,
        "Em frente a PUCCAMPINAS"
    )

    private val location2 = MarkerData(
        "LOCAL 2",
        LatLng(-22.8440713, -47.0531428),
        "Rua A Strazzacappa, 470 - Vila Embare, Valinhos - SP",
        4.9f,
        "Em frente ao Campinas Hall"
    )

    private lateinit var map: GoogleMap
    private lateinit var binding: FragmentMapsBinding
    private val locations = arrayListOf<MarkerData>(location1)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        functions = FirebaseFunctions.getInstance(firebaseApp, "southamerica-east1")
        getUnities()
        binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun getUnities(): Task<HttpsCallableResult> {
        return functions
            .getHttpsCallable("getAllUnities")
            .call()
            .addOnSuccessListener { task ->
                val result = task.data as JSONArray
                Log.d(TAG, "Resposta da função Firebase Functions: $result")

                // Aqui você pode continuar com o processamento da resposta
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Falha ao obter as localizações", exception)
            }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "Criado")
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

