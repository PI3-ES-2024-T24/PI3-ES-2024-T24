package com.puc.pi3_es_2024_t24

import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.google.firebase.firestore.firestore
import com.puc.pi3_es_2024_t24.databinding.FragmentSignUpBinding

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
        val db = Firebase.firestore
        //caso o botao de id btnSignUp é clicado
        binding.btnSignUp.setOnClickListener{
            //formata os dados
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val cpf = binding.etCpf.text.toString().trim()
            val birth = binding.etBirth.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            val pessoas = hashMapOf(
                "cpf" to cpf,
                "data_de_nascimento" to birth,
                "celular" to phone,
                "nome_completo" to name,
                "email" to email
            )
            if (validate()) {
                db.collection("pessoas")
                    .add(pessoas)
                    .addOnSuccessListener { documentReference ->
                        Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding document", e)
                    }
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        auth.currentUser?.sendEmailVerification()
                            ?.addOnSuccessListener {
                                Toast.makeText(requireContext(), "Para completar seu cadastro, verifique seu email!", Toast.LENGTH_SHORT).show()
                            }
                            ?.addOnFailureListener {
                                Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
                            }
                        Toast.makeText(
                            requireContext(),
                            "Conta criada com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()
                        navController.navigate(R.id.action_signUpFragment_to_signInFragment)

                    } else {
                        Log.e("error: ", it.exception.toString())
                    }
                }
            }
        }
        binding.btnLogin.setOnClickListener{
            it.findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
        }

        return binding.root
    }
    private fun validate(): Boolean {

        // Name field
        if (binding.etName.text.toString().isEmpty()) {
            binding.textInputLayoutName.error = "é necessario preencher esse campo"
            return false
        } else {
            binding.textInputLayoutName.error = null
        }

        // Email field
        val email = binding.etEmail.text.toString()
        if (email.isEmpty()) {
            binding.textInputLayoutEmail.error = "é necessario preencher esse campo"
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.textInputLayoutEmail.error = "porfavor digite um email valido"
            return false
        } else {
            binding.textInputLayoutEmail.error = null
        }

        // CPF field
        if (binding.etCpf.text.toString().isEmpty()) {
            binding.textInputLayoutCpf.error = "é necessario preencher esse campo"
            return false
        } else if (binding.etCpf.length() != 11) {
            binding.textInputLayoutCpf.error = "porfavor digite um cpf valido"
            return false
        } else {
            binding.textInputLayoutCpf.error = null
        }

        // Birth date field
        if (binding.etBirth.text.toString().isEmpty()) {
            binding.textInputLayoutBirth.error = "é necessario preencher esse campo"
            return false
        } else if (binding.etBirth.length() != 10) {
            binding.textInputLayoutBirth.error = "porfavor digite uma data de nascimento valida"
            return false
        } else {
            binding.textInputLayoutBirth.error = null
        }

        // Cell phone field
        if (binding.etPhone.text.toString().isEmpty()) {
            binding.textInputLayoutCelular.error = "é necessario preencher esse campo"
            return false
        }else {
            binding.textInputLayoutCelular.error = null
        }

        // Password field
        if (binding.etPassword.text.toString().isEmpty()) {
            binding.textInputLayoutPassword.error = "é necessario preencher esse campo"
            return false
        } else {
            binding.textInputLayoutPassword.error = null
        }

        // Confirm password field
        if (binding.etConfPassword.text.toString().isEmpty()) {
            binding.textInputLayoutConfirm.error = "é necessario preencher esse campo"
            return false
        } else if (binding.etPassword.text.toString() != binding.etConfPassword.text.toString()) {
            binding.textInputLayoutPassword.error = "senhas não coincidem"
            binding.textInputLayoutConfirm.error = "senhas não coincidem"
            return false
        } else {
            binding.textInputLayoutPassword.error = null
            binding.textInputLayoutConfirm.error = null
        }

        return true
    }
}