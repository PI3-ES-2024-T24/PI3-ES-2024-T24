package com.puc.pi3_es_2024_t24

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.puc.pi3_es_2024_t24.databinding.FragmentMapsBinding
import androidx.databinding.DataBindingUtil


class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mapView: SupportMapFragment
    private lateinit var googleMap: GoogleMap
    private val _markers = MutableLiveData<List<MarkerData>>()
    val markers: LiveData<List<MarkerData>> = _markers

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentMapsBinding>(
            inflater, R.layout.fragment_maps, container, false
        )
        binding.mapFragment = this
        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        // Add functionality for adding custom markers
        addCustomMarkers(listOf(
            MarkerData( "LOCAL 1",LatLng(-22.8345916,-47.0540574),   "Av. Profa. Ana Maria Silvestre Adade, 255-395 - Parque das Universidades, Campinas - SP", 4.9f,"Em frente a PUCCAMPINAS"),
            // Add more markers here
        ))
    }

    private fun addCustomMarkers(markers: List<MarkerData>) {
        _markers.value = markers.map { marker ->
            val markerOptions = MarkerOptions()
                .title(marker.name)
                .snippet(marker.address)
                .position(marker.latLng)
            googleMap.addMarker(markerOptions)
            marker
        }
    }
}
