package com.puc.pi3_es_2024_t24

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.puc.pi3_es_2024_t24.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    //temporario
    private val location1 = MarkerData(
        "LOCAL 1",
        LatLng(-22.8345916,-47.0540574),
        "Av. Profa. Ana Maria Silvestre Adade, 255-395 - Parque das Universidades, Campinas - SP",
        4.9f,
        "Em frente a PUCCAMPINAS"
    )

    private val location2 = MarkerData(
        "LOCAL 2",
        LatLng(-22.8440713,-47.0531428),
        "Rua A Strazzacappa, 470 - Vila Embare, Valinhos - SP",
        4.9f,
        "Em frente ao Campinas Hall"
    )

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentHomeBinding
    private val locations = arrayListOf<MarkerData>(location1, location2)
    private lateinit var map: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        val navController = findNavController()
        auth = Firebase.auth

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.bottom_credit_card ->Toast.makeText(requireContext(), "Fragmento pagamento!!!", Toast.LENGTH_SHORT).show()

                R.id.bottom_logout ->{
                    auth.signOut()
                    Toast.makeText(requireContext(), "Saiu da Conta!", Toast.LENGTH_SHORT).show()
                    navController.navigate(R.id.action_homeFragment_to_signInFragment)
                }
            }
            true
        }

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(ContentValues.TAG, "Criado")
        val homeFragment = childFragmentManager.findFragmentById(R.id.homeMaps) as SupportMapFragment
        homeFragment.getMapAsync { googleMap ->
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
        Log.d(ContentValues.TAG, "sincronizado")

    }
    fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        Log.d(ContentValues.TAG, "Mapa pronto")
        Log.d(ContentValues.TAG, "Marcador posicionado")
        val puc = LatLng(-22.83400, -47.05276)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(puc, 15f))
        Log.d(ContentValues.TAG, "loc inical")

    }


    private fun addMarkers(googleMap: GoogleMap) {
        locations.forEach { location ->
            val marker = googleMap.addMarker(
                MarkerOptions()
                    .title(location.name)
                    .snippet(location.address)
                    .position(location.latLng)
                    .icon(
                        BitmapHelper.vectorToBitmap(requireContext(), R.drawable.lock_icon, ContextCompat.getColor(requireContext(), androidx.appcompat.R.color.material_blue_grey_800))
                    )
            )
            marker?.tag = location
        }
    }
}