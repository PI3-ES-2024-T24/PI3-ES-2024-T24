package com.puc.pi3_es_2024_t24

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.puc.pi3_es_2024_t24.databinding.FragmentLocationBinding
import com.puc.pi3_es_2024_t24.databinding.FragmentSignInBinding

class LocationFragment : Fragment() {
    private lateinit var binding: FragmentLocationBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLocationBinding.inflate(inflater, container, false)
        return binding.root
    }
}