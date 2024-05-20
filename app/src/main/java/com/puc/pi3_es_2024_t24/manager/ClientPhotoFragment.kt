package com.puc.pi3_es_2024_t24.manager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.puc.pi3_es_2024_t24.R
import com.puc.pi3_es_2024_t24.databinding.FragmentClientPhotoBinding
import com.puc.pi3_es_2024_t24.databinding.FragmentQrCodeReadBinding

class ClientPhotoFragment : Fragment() {
private lateinit var binding: FragmentClientPhotoBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentClientPhotoBinding.inflate(inflater, container, false)
        binding.btn1access.setOnClickListener {
            findNavController()
        }
        binding.btn2access.setOnClickListener {

        }
        return binding.root
    }


}