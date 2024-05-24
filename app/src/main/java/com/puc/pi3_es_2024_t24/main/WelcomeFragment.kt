package com.puc.pi3_es_2024_t24.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import com.puc.pi3_es_2024_t24.R
import com.puc.pi3_es_2024_t24.databinding.FragmentWelcomeBinding
import com.puc.pi3_es_2024_t24.manager.ManagerActivity

class WelcomeFragment : Fragment() {

    private lateinit var binding: FragmentWelcomeBinding
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentWelcomeBinding.inflate(inflater, container, false)

        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        val navController = findNavController()
        auth = Firebase.auth

        Handler(Looper.getMainLooper()).postDelayed({
            val user = auth.currentUser
            if (user != null) {
                checkIfAdmin { isAdmin ->
                    if (isAdmin) {
                        val intent = Intent(requireContext(), ManagerActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()      
                    } else {
                        navController.navigate(R.id.action_welcomeFragment_to_nav_client)
                    }
                }
            } else {
                navController.navigate(R.id.action_welcomeFragment_to_signInFragment)
            }
        }, 2000)
        return binding.root
    }

    private fun checkIfAdmin(callback: (Boolean) -> Unit) {
        val user = auth.currentUser ?: return callback(false)
        db.collection("pessoas").document(user.uid).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val isAdmin = document.getBoolean("isAdmin") ?: false
                    callback(isAdmin)
                } else {
                    callback(false)
                }
            }
            .addOnFailureListener {
                callback(false)
            }
    }
}
