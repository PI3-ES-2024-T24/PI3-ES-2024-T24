package com.puc.pi3_es_2024_t24

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.puc.pi3_es_2024_t24.databinding.FragmentSignUpBinding
import com.puc.pi3_es_2024_t24.databinding.FragmentWelcomeBinding

class SignUpFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding:FragmentSignUpBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater, container, false)

        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        val navController = findNavController()
        auth = Firebase.auth
        //caso o botao de id btnSignUp é clicado
        binding.btnSignUp.setOnClickListener{
            //formata os dados
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        navController.navigate(R.id.action_signUpFragment_to_signInFragment2)

                    } else {
                        Log.e("error: ", it.exception.toString())
                    }
                }
        }
        binding.btnLogin.setOnClickListener{
            it.findNavController().navigate(R.id.action_signUpFragment_to_signInFragment2)
        }

        return binding.root
    }

}