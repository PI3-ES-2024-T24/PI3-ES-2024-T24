package com.puc.pi3_es_2024_t24

import android.os.Bundle
import android.util.Log
import android.util.Patterns
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.puc.pi3_es_2024_t24.databinding.FragmentSignInBinding

class SignInFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentSignInBinding
    private lateinit var db : FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignInBinding.inflate(inflater, container, false)

        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        val navController = findNavController()
        auth = Firebase.auth

        db = Firebase.firestore

        binding.btnSignIn.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            if (validate()) {
                //função do firebase auth para logar com email e senha
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val verifyemail = auth.currentUser?.isEmailVerified
                        if(verifyemail == true){
                            Toast.makeText(requireContext(), "Sucesso ao entrar na conta!",Toast.LENGTH_SHORT).show()
                            navController.navigate(R.id.action_signInFragment_to_homeFragment)
                        }else{
                            Toast.makeText(requireContext(),"Conta não verificada, verfique no seu email",Toast.LENGTH_SHORT).show()
                        }
                    }else {
                        //cria um log do nivel E (error) no LogCat
                        Log.e("error: ", it.exception.toString())
                    }
                }
            }
        }
        binding.btnMaps.setOnClickListener{
            it.findNavController().navigate(R.id.action_signInFragment_to_mapsFragment)
        }

        binding.btnRegister.setOnClickListener{
            it.findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }
        binding.forgotPassword.setOnClickListener {
            it.findNavController().navigate(R.id.action_signInFragment_to_forgotPasswordFragment)
        }
        return binding.root
    }

    private fun validate(): Boolean{
        val email = binding.etEmail.text.toString()
        if(binding.etEmail.text.toString() == ""){
            binding.textInputLayoutEmail.error = "é necessario preencher esse campo"
            return false
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.textInputLayoutEmail.error = "porfavor digite um email valido"
            return false
        }
        if(binding.etPassword.text.toString() == ""){
            binding.textInputLayoutPassword.error = "é necessario preencher esse campo"
            return false
        }
        return true
    }
}