package com.puc.pi3_es_2024_t24.manager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.puc.pi3_es_2024_t24.R
import com.puc.pi3_es_2024_t24.databinding.FragmentClientAccessBinding
import org.json.JSONObject

class ClientAccessFragment : Fragment() {
private lateinit var binding: FragmentClientAccessBinding
    private lateinit var firestore: FirebaseFirestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        firestore = FirebaseFirestore.getInstance()
        // Inflate the layout for this fragment
        binding = FragmentClientAccessBinding.inflate(inflater, container, false)
        binding.btn1access.setOnClickListener {
            val action = ClientAccessFragmentDirections.actionClientAccessFragmentToCameraFragment(1)
            findNavController().navigate(action)        }
        binding.btn2access.setOnClickListener {
            val action = ClientAccessFragmentDirections.actionClientAccessFragmentToCameraFragment(2)
            findNavController().navigate(action)
        }
        binding.btnRescan.setOnClickListener {
            findNavController().navigate(R.id.action_clientAccessFragment_to_cameraFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = ClientAccessFragmentArgs.fromBundle(requireArguments())
        val argQr = args.qrCodeInfo
        if (argQr != "null"){
            val json = argQr?.let { JSONObject(it) }
            val clientId = json?.getString("clientId")
            if (clientId != null) {
                fetchClientInfo(clientId)
            }
        }

    }
    private fun fetchClientInfo(clientId: String) {
        firestore.collection("pessoas").document(clientId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val name = document.getString("nome_completo")
                    val email = document.getString("email")
                    // Do something with the retrieved name and email
                    binding.etName.text = name
                    binding.etEmail.text = email
                } else {
                    // Handle case where the document does not exist
                }
            }
            .addOnFailureListener { exception ->
                // Handle any errors
            }
    }
}