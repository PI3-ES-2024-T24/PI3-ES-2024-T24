package com.puc.pi3_es_2024_t24

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.puc.pi3_es_2024_t24.databinding.FragmentSignUpBinding
import com.puc.pi3_es_2024_t24.databinding.FragmentWelcomeBinding

class SignUpFragment : Fragment() {

    private lateinit var binding:FragmentSignUpBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater, container, false)

        (activity as? AppCompatActivity)?.supportActionBar?.hide()

        return binding.root
    }

}