package com.puc.pi3_es_2024_t24.manager

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.puc.pi3_es_2024_t24.R
import com.puc.pi3_es_2024_t24.databinding.FragmentLocationSuccessBinding
import com.puc.pi3_es_2024_t24.databinding.FragmentMenuManagerBinding
import com.puc.pi3_es_2024_t24.main.MainActivity

class MenuManagerFragment : Fragment() {
    private lateinit var auth: FirebaseAuth

    private lateinit var binding: FragmentMenuManagerBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        auth = Firebase.auth
        binding = FragmentMenuManagerBinding.inflate(inflater, container, false)

        binding.btnReleaseLocker.setOnClickListener {
            findNavController().navigate(R.id.action_menuManagerFragment_to_cameraFragment)
        }
        binding.btnOpenLocker.setOnClickListener {

        }

        binding.btnLogOutManager.setOnClickListener {
            val intent = Intent(requireContext(),MainActivity::class.java)
            startActivity(intent)
            auth.signOut()
            requireActivity().finish()
        }
        return binding.root
    }

}