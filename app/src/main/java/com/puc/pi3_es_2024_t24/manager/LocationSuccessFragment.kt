package com.puc.pi3_es_2024_t24.manager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.puc.pi3_es_2024_t24.R
import com.puc.pi3_es_2024_t24.databinding.FragmentConfirmLockerBinding
import com.puc.pi3_es_2024_t24.databinding.FragmentLocationSuccessBinding

class LocationSuccessFragment : Fragment() {

    private lateinit var binding : FragmentLocationSuccessBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLocationSuccessBinding.inflate(inflater, container, false)
        binding.btnBack.setOnClickListener{
            it.findNavController().navigate(R.id.action_locationSuccessFragment_to_confirmLockerFragment)
        }

        return binding.root
    }
}