package com.puc.pi3_es_2024_t24.main

import android.app.Dialog
import android.app.PendingIntent
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.nfc.tech.NfcA
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.puc.pi3_es_2024_t24.R
import com.puc.pi3_es_2024_t24.databinding.DialogNfcBinding
import com.puc.pi3_es_2024_t24.databinding.DialogPaymentBinding
import com.puc.pi3_es_2024_t24.databinding.FragmentSignInBinding
import com.puc.pi3_es_2024_t24.models.NfcTag
import com.puc.pi3_es_2024_t24.models.QrCode
import org.json.JSONObject
import java.nio.charset.Charset

class SignInFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentSignInBinding
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        val navController = findNavController()
        auth = Firebase.auth

        db = Firebase.firestore
        binding.btnSignIn.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            if (validate()) {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val verifyemail = auth.currentUser?.isEmailVerified
                        if (verifyemail == true) {
                            navController.navigate(R.id.action_signInFragment_to_nav_client)
                        } else {
                            Toast.makeText(requireContext(), "Conta não verificada, verfique no seu email", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        binding.textInputLayoutEmail.error = "Email e/ou senha errados"
                        Log.e("error: ", it.exception.toString())
                    }
                }
            }
        }
        binding.btnMaps.setOnClickListener {
            it.findNavController().navigate(R.id.action_signInFragment_to_mapsFragment)
        }
        binding.btnRegister.setOnClickListener {
            it.findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }
        binding.forgotPassword.setOnClickListener {
            it.findNavController().navigate(R.id.action_signInFragment_to_forgotPasswordFragment)
        }
        binding.testCamera.setOnClickListener {
            it.findNavController().navigate(R.id.action_signInFragment_to_nav_manager)
        }
        binding.testNfc.setOnClickListener{
            it.findNavController().navigate(R.id.action_signInFragment_to_confirmLockerFragment)
        }
        return binding.root
    }

    private fun validate(): Boolean {
        val email = binding.etEmail.text.toString()
        if (binding.etEmail.text.toString() == "") {
            binding.textInputLayoutEmail.error = "é necessario preencher esse campo"
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.textInputLayoutEmail.error = "porfavor digite um email valido"
            return false
        }
        if (binding.etPassword.text.toString() == "") {
            binding.textInputLayoutPassword.error = "é necessario preencher esse campo"
            return false
        }
        return true
    }
}
