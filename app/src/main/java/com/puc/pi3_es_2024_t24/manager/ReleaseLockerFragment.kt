package com.puc.pi3_es_2024_t24.manager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.puc.pi3_es_2024_t24.R
import com.puc.pi3_es_2024_t24.databinding.FragmentReleaseLockerBinding

class ReleaseLockerFragment : Fragment() {

    private lateinit var binding : FragmentReleaseLockerBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentReleaseLockerBinding.inflate(inflater, container, false)

        binding.btnOpen.setOnClickListener{
            Toast.makeText(requireContext(), "Armário aberto!", Toast.LENGTH_SHORT).show()
        }

        binding.btnClose.setOnClickListener {
            findNavController().navigate(R.id.action_releaseLockerFragment_to_closeLocationFragment)
        }

        return binding.root
    }

}