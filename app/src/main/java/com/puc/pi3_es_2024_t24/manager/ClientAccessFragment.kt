package com.puc.pi3_es_2024_t24.manager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.puc.pi3_es_2024_t24.R
import com.puc.pi3_es_2024_t24.databinding.FragmentClientAccessBinding
import org.json.JSONObject

class ClientAccessFragment : Fragment() {
private lateinit var binding: FragmentClientAccessBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
            val json = JSONObject(argQr)
            val email = json.getString("clientId")
            binding.etEmail.text = email
        }

    }
}