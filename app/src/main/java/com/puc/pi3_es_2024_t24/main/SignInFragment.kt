package com.puc.pi3_es_2024_t24.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.puc.pi3_es_2024_t24.R
import com.puc.pi3_es_2024_t24.databinding.FragmentSignInBinding
import com.puc.pi3_es_2024_t24.manager.ManagerActivity

class SignInFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentSignInBinding
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        auth = Firebase.auth

        binding.btnSignIn.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            if (validate(email, password)) {
                signInUser(email, password)
            }
        }

        binding.btnMaps.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_mapsFragment)
        }
        binding.btnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }
        binding.forgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_forgotPasswordFragment)
        }
        return binding.root
    }

    private fun validate(email: String, password: String): Boolean {
        return when {
            email.isEmpty() -> {
                binding.textInputLayoutEmail.error = "É necessário preencher esse campo"
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.textInputLayoutEmail.error = "Por favor, digite um email válido"
                false
            }
            password.isEmpty() -> {
                binding.textInputLayoutPassword.error = "É necessário preencher esse campo"
                false
            }
            else -> {
                binding.textInputLayoutEmail.error = null
                binding.textInputLayoutPassword.error = null
                true
            }
        }
    }

    private fun signInUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                if (user?.isEmailVerified == true) {
                    checkIfAdmin { isAdmin ->
                        findNavController().navigate(
                            if (isAdmin) R.id.action_signInFragment_to_nav_manager
                            else R.id.action_signInFragment_to_nav_client
                        )
                    }
                } else {
                    Toast.makeText(requireContext(), "Conta não verificada, verifique no seu email", Toast.LENGTH_SHORT).show()
                }
            } else {
                binding.textInputLayoutEmail.error = "Email e/ou senha errados"
                Log.e("error: ", task.exception.toString())
            }
        }
    }

    private fun checkIfAdmin(callback: (Boolean) -> Unit) {
        val user = auth.currentUser ?: return callback(false)
        db.collection("pessoas").document(user.uid).get()
            .addOnSuccessListener { document ->
                callback(document?.getBoolean("isAdmin") == true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }
}
