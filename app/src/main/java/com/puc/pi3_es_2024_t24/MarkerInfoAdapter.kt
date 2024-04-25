package com.puc.pi3_es_2024_t24

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class MarkerInfoAdapter(private val context: Context): GoogleMap.InfoWindowAdapter {

    override fun getInfoWindow(marker: Marker): View? = null

    @SuppressLint("InflateParams")
    override fun getInfoContents(marker: Marker): View? {
        val location = marker.tag as? MarkerData ?: return null

        val view = LayoutInflater.from(context).inflate(
            R.layout.custom_marker_info,
            null
        )

        view.findViewById<TextView>(R.id.txt_title).text = location.name
        view.findViewById<TextView>(R.id.txt_address).text = location.address
        view.findViewById<TextView>(R.id.txt_reference).text = location.reference
        return view
    }
}