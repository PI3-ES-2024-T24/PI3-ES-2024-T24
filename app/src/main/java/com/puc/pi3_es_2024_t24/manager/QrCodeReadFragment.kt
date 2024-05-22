package com.puc.pi3_es_2024_t24.manager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.puc.pi3_es_2024_t24.R
import com.puc.pi3_es_2024_t24.databinding.FragmentQrCodeReadBinding
import org.json.JSONObject

class QrCodeReadFragment : Fragment() {

    private lateinit var binding: FragmentQrCodeReadBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentQrCodeReadBinding.inflate(inflater, container, false)
        binding.btnRescan.setOnClickListener {
            findNavController().navigate(R.id.action_qrCodeReadFragment_to_cameraFragment)
        }
        binding.btnConfirm.setOnClickListener {
            findNavController().navigate(R.id.action_qrCodeReadFragment_to_clientPhotoFragment)

        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = QrCodeReadFragmentArgs.fromBundle(requireArguments())
        val argQr = args.qrCodeInfo
        if (argQr != "null"){
            val json = JSONObject(argQr)
            val email = json.getString("clientId")
            binding.etEmail.text = email
        }

    }


}