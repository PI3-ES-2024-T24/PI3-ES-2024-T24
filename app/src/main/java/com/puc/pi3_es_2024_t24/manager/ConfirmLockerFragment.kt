package com.puc.pi3_es_2024_t24.manager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.puc.pi3_es_2024_t24.R
import com.puc.pi3_es_2024_t24.databinding.FragmentConfirmLockerBinding

class ConfirmLockerFragment : Fragment() {
    private lateinit var binding:FragmentConfirmLockerBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentConfirmLockerBinding.inflate(inflater, container, false)

        return binding.root
    }

}