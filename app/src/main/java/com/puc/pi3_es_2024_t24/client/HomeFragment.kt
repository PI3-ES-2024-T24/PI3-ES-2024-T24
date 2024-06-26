package com.puc.pi3_es_2024_t24.client

import android.Manifest
import android.app.Dialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.puc.pi3_es_2024_t24.utils.BitmapHelper
import com.puc.pi3_es_2024_t24.utils.MarkerInfoAdapter
import com.puc.pi3_es_2024_t24.R
import com.puc.pi3_es_2024_t24.databinding.DialogCardBinding
import com.puc.pi3_es_2024_t24.databinding.DialogLocationBinding
import com.puc.pi3_es_2024_t24.databinding.DialogPaymentBinding
import com.puc.pi3_es_2024_t24.databinding.DialogQrcodeBinding
import com.puc.pi3_es_2024_t24.databinding.FragmentHomeBinding
import com.puc.pi3_es_2024_t24.models.Card
import com.puc.pi3_es_2024_t24.models.Client
import com.puc.pi3_es_2024_t24.models.MarkerData
import com.puc.pi3_es_2024_t24.models.QrCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class HomeFragment : Fragment(), OnMapReadyCallback {


    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentHomeBinding
    private lateinit var bindingPayment : DialogPaymentBinding
    private lateinit var bindingCard : DialogCardBinding
    private lateinit var bindingLocation: DialogLocationBinding
    private lateinit var bindingQrCode: DialogQrcodeBinding
    private val locations = arrayListOf<MarkerData>()
    private lateinit var locationDialog: Dialog
    private lateinit var qrCodeDialog: Dialog
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var currentLocation: Location
    private var currentActiveLocation: Boolean = false
    private lateinit var functions: FirebaseFunctions
    private val firebaseApp = FirebaseApp.getInstance()
    private val db = Firebase.firestore
    private lateinit var client: Client
    private var clicked = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        functions = FirebaseFunctions.getInstance(firebaseApp, "southamerica-east1")
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        bindingPayment = DialogPaymentBinding.inflate(inflater, container, false)
        bindingCard = DialogCardBinding.inflate(inflater, container, false)
        bindingLocation = DialogLocationBinding.inflate(inflater, container, false)
        bindingQrCode = DialogQrcodeBinding.inflate(inflater, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        getLocation()
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        val navController = findNavController()
        auth = Firebase.auth
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.bottom_credit_card ->{
                    showPayDialogBox()
                }
                R.id.bottom_logout ->{
                    showLogoutDialogBox()
                }
            }
            true
        }
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentActiveLocation = loadLocationState()
        // caso tenha uma locação pendente chama o dialogo do qr code
        if (currentActiveLocation){
            showQrCode()
        }
        loadClient()
        Log.d(ContentValues.TAG, "Cliente carregado")
    }
    private fun getLocation() {
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
                        location?.let {
                            currentLocation = it
                            val homeMapFragment = childFragmentManager.findFragmentById(R.id.homeMaps) as SupportMapFragment
                            homeMapFragment.getMapAsync(this)
                        } ?: run {
                            // Handle case where location is null
                            Toast.makeText(requireContext(), "Não foi possível obter a localização", Toast.LENGTH_SHORT).show()
                        }
                    }
                }else -> {
                // No location access granted.
            }
            }
        }

        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
    override fun onMapReady(googleMap: GoogleMap) {
        val currentLoc= LatLng(currentLocation.latitude,currentLocation.longitude)
        map = googleMap
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc,15f))
        getUnities()
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
        map.isMyLocationEnabled = true
        map.setOnMarkerClickListener { marker ->
            marker.showInfoWindow()
            val location = marker.tag as MarkerData
            binding.fabExpand.show()
            binding.fabExpand.setOnClickListener{
                if (!clicked){
                    binding.fabNavigation.show()
                    val distance = calculateDistance(currentLoc, marker.position)
                    if (distance < 1000) {
                        binding.fabLocation.show()
                    }
                }
                else{
                    binding.fabNavigation.hide()
                    binding.fabLocation.hide()
                }
                clicked = !clicked
            }
            binding.fabNavigation.setOnClickListener{
                navIntent(marker.position)
            }
            binding.fabLocation.setOnClickListener {
                if (client.card?.nome == "null" || client.card?.nome == null) {
                    Toast.makeText(requireContext(),"É necessario ter um cartão registrado para começar a alugar!!",Toast.LENGTH_SHORT).show()
                    showPayDialogBox()
                }
                else{
                    showLocationDialog(location.unityId)
                    initPrices(location.unityId)
                }
            }
            true
        }
        map.setOnMapClickListener {
            binding.fabExpand.hide()
            binding.fabNavigation.hide()
            binding.fabLocation.hide()
            clicked = false
        }

    }
    //puxa do firestore os preços da hora da unidade
    private fun initPrices(unityId: String){
        db.collection("unidades")
            .whereEqualTo("unityId", unityId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(TAG, "documentos")
                    Log.d(TAG, "${document.id} => ${document.data}")
                    val precos = document.get("precos") as? Map<*, *>
                    bindingLocation.txt30.text = "Preço: ${precos?.get("30")} R$"
                    bindingLocation.txt1.text = "Preço: ${precos?.get("60")} R$"
                    bindingLocation.txt2.text = "Preço: ${precos?.get("120")} R$"
                    bindingLocation.txt4.text = "Preço: ${precos?.get("240")} R$"
                    bindingLocation.txt18.text = "Preço: ${precos?.get("18h")} R$"
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
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
            dialog.dismiss()
        }
        dialog.show()
    }
    private fun showLocationDialog(unityId:String) {
        if (!::locationDialog.isInitialized) {
            locationDialog = Dialog(requireContext())
            locationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            locationDialog.setCancelable(false)
            locationDialog.setContentView(bindingLocation.root)
            locationDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            check18hr()
            bindingLocation.btnConfirm.setOnClickListener {
                var option = 0
                when (bindingLocation.radioGroupLocation.checkedRadioButtonId) {
                    bindingLocation.radio30min.id -> option = 30
                    bindingLocation.radio1hr.id -> option = 1
                    bindingLocation.radio2hr.id -> option = 2
                    bindingLocation.radio4hr.id -> option = 4
                    bindingLocation.radio18hr.id -> option = 18
                }
                currentActiveLocation = true
                saveLocationState(currentActiveLocation)
                locationDialog.dismiss()
                makeJsonQr(unityId, option)
                showQrCode()

            }
            bindingLocation.btnCancel.setOnClickListener {
                locationDialog.dismiss()
            }
        }
        locationDialog.show()
    }
    private fun isBetween7And8(): Boolean {
        val currentTime = Calendar.getInstance()
        val hour = currentTime.get(Calendar.HOUR_OF_DAY)

        return hour in 7..7
    }
    private fun check18hr(){
        if (isBetween7And8()){
            bindingLocation.radio18hr.isClickable = true
            bindingLocation.txtTime18.visibility = View.INVISIBLE
        }
        else{
            bindingLocation.radio18hr.isClickable = false
            bindingLocation.txtTime18.visibility = View.VISIBLE
        }
    }
    private fun showCancelDialog(){
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_cancel)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnNotCancel : Button = dialog.findViewById(R.id.btnNotCancel)
        val btnCancel : Button = dialog.findViewById(R.id.btnCancel)

        btnNotCancel.setOnClickListener {
            dialog.dismiss()
        }
        btnCancel.setOnClickListener {
            dialog.dismiss()
            qrCodeDialog.dismiss()
            currentActiveLocation = false
            saveLocationState(currentActiveLocation)
        }
        dialog.show()
    }
    private fun showQrCode() {
        if (!::qrCodeDialog.isInitialized) {
            qrCodeDialog = Dialog(requireContext())
            qrCodeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            qrCodeDialog.setCancelable(false)
            qrCodeDialog.setContentView(bindingQrCode.root)
            qrCodeDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val savedContent = loadQRCodeContent()
            bindingQrCode.qrCodeImg.setImageBitmap(savedContent?.let { generateQRCode(it, 800, 800) })
            bindingQrCode.btnCancel.setOnClickListener {
                showCancelDialog()
            }
        }
        qrCodeDialog.show()
    }
    private fun makeJsonQr(unityId: String, option: Int){
        val userUid = auth.currentUser?.uid
        if (userUid!= null) {
            val qrcode = QrCode(unityId, userUid, option)
            val gson = Gson()
            val content = gson.toJson(qrcode)
            saveQRCodeContent(content)
        }
    }
    private fun generateQRCode(content: String, width: Int, height: Int): Bitmap? {
        try {
            val bitMatrix: BitMatrix = QRCodeWriter().encode(
                content,
                BarcodeFormat.QR_CODE,
                width,
                height,
                null
            )
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) 0xFF000000.toInt() else 0xFFFFFFFF.toInt())
                }
            }
            return bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
        }
        return null
    }
    private fun showPayDialogBox() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val cpf: String = client.cpf

        val bindingPayment = DialogPaymentBinding.inflate(layoutInflater)
        val bindingCard = DialogCardBinding.inflate(layoutInflater)

        if (client.card?.nome == "null" || client.card?.nome == null) {
            dialog.setContentView(bindingPayment.root)

            bindingPayment.btnSave.setOnClickListener {
                saveCard(
                    cpf,
                    bindingPayment.etCardName.text.toString(),
                    bindingPayment.etCardNumber.text.toString(),
                    bindingPayment.etCardValidation.text.toString(),
                    bindingPayment.etCardCCV.text.toString()
                )
                dialog.dismiss()
            }
            bindingPayment.btnCancel.setOnClickListener {
                dialog.dismiss()
            }
        } else {
            dialog.setContentView(bindingCard.root)
            bindingCard.tvCardNumber.text = client.card?.numero

            bindingCard.btnAddCard.setOnClickListener {
                dialog.dismiss()
                showPayDialogBox()
            }
            bindingCard.btnCancel.setOnClickListener {
                dialog.dismiss()
            }
            bindingCard.btnDeleteCard.setOnClickListener {
                saveCard(cpf, "null", "null", "null", "null")
                dialog.dismiss()
            }
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
    private fun calculateDistance(userLocation: LatLng, markerLatLng: LatLng): Float {

        val locationResult = FloatArray(3)
        Location.distanceBetween(userLocation.latitude, userLocation.longitude,
            markerLatLng.latitude, markerLatLng.longitude, locationResult)
        return locationResult[0]
    }
    private fun saveCard(cpf: String, cardName: String, cardNumber: String, cardValidation: String, cardCVV: String) : Task<Unit> {
        val body = hashMapOf(
            "cpf" to cpf,
            "nome" to cardName,
            "numero" to cardNumber,
            "validade" to cardValidation,
            "cvv" to "null"
        )

        return functions
            .getHttpsCallable("updateCard")
            .call(body) // Passa diretamente o objClient
            .continueWith{task ->
                if (task.isSuccessful) {
                    client.card = Card(cardCVV, cardName, cardNumber, cardValidation)
                    Toast.makeText(
                        requireContext(),
                        "Cartão atualizado com sucesso!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val exception = task.exception
                    Log.e("UPDATE", "Error updating card", exception)
                    Toast.makeText(
                        requireContext(),
                        "Falha ao atualizar cartão!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
    private fun loadClient() {
        CoroutineScope(Dispatchers.IO).launch {
            val email = auth.currentUser?.email
            var card: Card?

            auth.currentUser?.uid?.let {
                db.collection("pessoas")
                    .document(it)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document.get("cartao") == null) {
                            card = null
                        } else {
                            val cardDoc = document.get("cartao") as? Map<String, Any>
                            card = Card(cardDoc?.get("cvv").toString(), cardDoc?.get("nome").toString(), cardDoc?.get("numero").toString(), cardDoc?.get("validade").toString())
                        }
                        client = Client(
                            document.getString("cpf").toString(),
                            document.getString("email").toString(),
                            document.getString("nome").toString(),
                            document.getString("dataNascimento").toString(),
                            document.getString("celular").toString(),
                            card
                        )
                    }
            }
        }
    }
    private fun saveLocationState(active: Boolean) {
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("active_location", active)
            apply()
        }
    }
    private fun loadLocationState(): Boolean {
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        return sharedPref.getBoolean("active_location", false)
    }
    private fun saveQRCodeContent(content: String) {
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("qr_code_content", content)
            apply()
        }
    }
    private fun loadQRCodeContent(): String? {
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        return sharedPref.getString("qr_code_content", null)
    }


}