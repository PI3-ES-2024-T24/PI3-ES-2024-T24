package com.puc.pi3_es_2024_t24

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity: AppCompatActivity() {

    private val location1 = Location(
        "LOCAL 1",
        LatLng(-22.8345916,-47.0540574),
        "Av. Profa. Ana Maria Silvestre Adade, 255-395 - Parque das Universidades, Campinas - SP",
        4.9f
    )

    private val location2 = Location(
        "LOCAL 2",
        LatLng(-22.8440713,-47.0531428),
        "Rua A Strazzacappa, 470 - Vila Embare, Valinhos - SP",
        4.9f
    )

    private val locations = arrayListOf<Location>(location1, location2) // PUXAR INFORMAÇÕES DAS LOCALIZAÇÕES DO FIREBASE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
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

    private fun addMarkers(googleMap: GoogleMap) {
        locations.forEach { location ->
            val marker = googleMap.addMarker(
                MarkerOptions()
                    .title(location.name)
                    .snippet(location.address)
                    .position(location.latLng)
                    .icon(
                        BitmapHelper.vectorToBitmap(this, R.drawable.baseline_lock_24, ContextCompat.getColor(this, androidx.appcompat.R.color.material_blue_grey_800))
                    )
            )
        }
    }
}

data class Location(
    val name: String,
    val latLng: LatLng,
    val address: String,
    val rating: Float
    )