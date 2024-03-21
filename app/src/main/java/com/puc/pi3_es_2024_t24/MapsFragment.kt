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


class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var binding: FragmentMapsBinding
    private val _markers = MutableLiveData<List<MarkerData>>()
    val markers: LiveData<List<MarkerData>> = _markers

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "Criado")
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        Log.d(TAG, "sincronizado")

    }
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        Log.d(TAG, "Mapa pronto")
        addCustomMarkers(listOf(MarkerData( "LOCAL 1",LatLng(-22.8345916,-47.0540574), "Av. Profa. Ana Maria Silvestre Adade, 255-395 - Parque das Universidades, Campinas - SP", 4.9f,"Em frente a PUCCAMPINAS")))
        Log.d(TAG, "Marcador posicionado")
        val puc = LatLng(-22.83400, -47.05276)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(puc, 15f))
        Log.d(TAG, "loc inical")

    }


    private fun addCustomMarkers(markers: List<MarkerData>) {
        _markers.value = markers.map { marker ->
            val markerOptions = MarkerOptions()
                .title(marker.name)
                .snippet(marker.address)
                .position(marker.latLng)
                .icon(
                    BitmapHelper.vectorToBitmap(requireContext(), R.drawable.lock_icon, ContextCompat.getColor(requireContext(), androidx.appcompat.R.color.material_blue_grey_800))
                )
            map.addMarker(markerOptions)
            marker
        }
    }
}

