package com.puc.pi3_es_2024_t24.manager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.puc.pi3_es_2024_t24.R
import com.puc.pi3_es_2024_t24.databinding.FragmentCloseLocationBinding

class CloseLocationFragment : Fragment() {

    private lateinit var binding : FragmentCloseLocationBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCloseLocationBinding.inflate(inflater, container, false)

        binding.btnClose.setOnClickListener{
            // ENCERRAR LOCAÇÃO
        }

        binding.btnBack.setOnClickListener{
            findNavController().navigate(R.id.action_closeLocationFragment_to_releaseLockerFragment)
        }

        return binding.root
    }

}