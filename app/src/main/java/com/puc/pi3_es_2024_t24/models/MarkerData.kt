package com.puc.pi3_es_2024_t24.models

import com.google.android.gms.maps.model.LatLng

data class MarkerData(val unityId:String,
                      val name: String,
                      val latLng: LatLng,
                      val address: String,
                      val rating: Float,
                      val reference: String)
