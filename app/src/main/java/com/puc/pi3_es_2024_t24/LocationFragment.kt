package com.puc.pi3_es_2024_t24

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.puc.pi3_es_2024_t24.databinding.FragmentLocationBinding
import com.puc.pi3_es_2024_t24.databinding.FragmentSignInBinding

class LocationFragment : Fragment() {
    private lateinit var binding: FragmentLocationBinding
    private var optionTime: Int? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLocationBinding.inflate(inflater, container, false)
        binding.btnConfirm.setOnClickListener{
            if (optionTime != null){
                Toast.makeText(requireContext(), "confirmed!",Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(requireContext(), "Select at least one option!",Toast.LENGTH_SHORT).show()
            }
        }
        binding.b30.setOnClickListener {
            optionTime = 30
            Toast.makeText(requireContext(), "Selected 30!",Toast.LENGTH_SHORT).show()
        }
        binding.b1.setOnClickListener {
            optionTime = 1
            Toast.makeText(requireContext(), "Selected 1!",Toast.LENGTH_SHORT).show()
        }
        return binding.root
    }
}