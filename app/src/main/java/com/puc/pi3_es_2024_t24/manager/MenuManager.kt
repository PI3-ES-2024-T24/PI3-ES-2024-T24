package com.puc.pi3_es_2024_t24.manager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.puc.pi3_es_2024_t24.R
import com.puc.pi3_es_2024_t24.databinding.FragmentMenuManagerBinding
import com.puc.pi3_es_2024_t24.databinding.FragmentQrCodeReadBinding


class MenuManagerFragment : Fragment() {

    private lateinit var binding: FragmentMenuManagerBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentMenuManagerBinding.inflate(inflater, container, false)
        binding.btnReleaseLocker.setOnClickListener {
            findNavController().navigate(R.id.)
        }
        binding.btnOpenLocker.setOnClickListener {
            findNavController().navigate(R.id.)

        }

        binding.btnLogOutManager.setOnClickListener {
            findNavController().navigate(R.id.)
        }
        
        return binding.root
    }

    }
}
