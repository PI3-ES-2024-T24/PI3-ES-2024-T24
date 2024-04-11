package com.puc.pi3_es_2024_t24

import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.functions.FirebaseFunctions
import com.puc.pi3_es_2024_t24.databinding.FragmentHomeBinding
import com.puc.pi3_es_2024_t24.databinding.FragmentMapsBinding

class HomeFragment : Fragment(), OnMapReadyCallback {


    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentHomeBinding
    private val locations = arrayListOf<MarkerData>()
    private lateinit var map: GoogleMap
    private lateinit var functions: FirebaseFunctions
    private val firebaseApp = FirebaseApp.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        functions = FirebaseFunctions.getInstance(firebaseApp, "southamerica-east1")
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val homeMapFragment = childFragmentManager.findFragmentById(R.id.homeMaps) as SupportMapFragment
        homeMapFragment.getMapAsync(this)
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        val navController = findNavController()
        auth = Firebase.auth

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.bottom_credit_card ->Toast.makeText(requireContext(), "Fragmento pagamento!!!", Toast.LENGTH_SHORT).show()

                R.id.bottom_logout ->{
                    Toast.makeText(requireContext(), "dialog logout", Toast.LENGTH_SHORT).show()
                    showLogoutDialogBox()
                }
            }
            true
        }

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(ContentValues.TAG, "Criado")
        Log.d(ContentValues.TAG, "sincronizado")

    }
    override fun onMapReady(googleMap: GoogleMap) {
        val puc = LatLng(-22.83400, -47.05276)
        map = googleMap
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(puc, 15f))
        getUnities()

        map.setOnMarkerClickListener { marker ->
            marker.showInfoWindow()
            true
        }
        map.setOnMapClickListener {
        }

    }
    private fun getUnities(): Task<Unit> {
        return functions
            .getHttpsCallable("getAllUnities")
            .call()
            .continueWith { task ->
                locations.clear()
                val result = task.result?.data as? List<Map<String, Any>>

                if (result == null) {
                    Log.e(ContentValues.TAG, "Resposta inválida da função Firebase Functions")
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
                val homeFragment = childFragmentManager.findFragmentById(R.id.homeMaps) as SupportMapFragment
                homeFragment.getMapAsync { googleMap ->
                    googleMap.setInfoWindowAdapter(MarkerInfoAdapter(requireContext()))
                    addMarkers(googleMap)
                }
            }
            .addOnFailureListener { exception ->
                Log.e(ContentValues.TAG, "Falha ao obter as localizações", exception)
            }
    }
    private fun showLogoutDialogBox(){
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_signout)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnLogout : Button = dialog.findViewById(R.id.btnLogout)
        val btnCancel : Button = dialog.findViewById(R.id.btnCancel)

        btnLogout.setOnClickListener {
            Toast.makeText(requireContext(), "Saiu da Conta", Toast.LENGTH_SHORT).show()
            auth.signOut()
            dialog.dismiss()
            findNavController().navigate(R.id.action_homeFragment_to_signInFragment)
        }
        btnCancel.setOnClickListener {
            Toast.makeText(requireContext(), "Cancelou", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        dialog.show()
    }
    private fun navIntent(location: LatLng) {
        val intent =
            Uri.parse("google.navigation:q=${location.latitude}, ${location.longitude}&mode=w")
        val mapIntent = Intent(Intent.ACTION_VIEW, intent)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
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