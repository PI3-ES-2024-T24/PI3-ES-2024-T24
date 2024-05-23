package com.puc.pi3_es_2024_t24.manager

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.puc.pi3_es_2024_t24.R
import com.puc.pi3_es_2024_t24.databinding.FragmentConfirmLockerBinding
import com.puc.pi3_es_2024_t24.databinding.FragmentLocationSuccessBinding
import com.puc.pi3_es_2024_t24.main.MainActivity

class LocationSuccessFragment : Fragment() {

    private lateinit var binding : FragmentLocationSuccessBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLocationSuccessBinding.inflate(inflater, container, false)
        binding.btnBack.setOnClickListener{
            findNavController().navigate(R.id.action_locationSuccessFragment_to_menuManagerFragment)
        }

        return binding.root
    }
}