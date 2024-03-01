package com.puc.pi3_es_2024_t24

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.puc.pi3_es_2024_t24.databinding.ActivitySignUpBinding
import com.puc.pi3_es_2024_t24.ui.theme.PI3ES2024T24Theme

class SignUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        val db = Firebase.firestore

        binding.btnSignUp.setOnClickListener{
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val name = binding.etName.text.toString().trim()
            val cpf = binding.etCpf.text.toString().trim()
            val birth = binding.etBirth.text.toString().trim()
            val celular = binding.etCel.text.toString().trim()
            
            val pessoas = hashMapOf(
                "nome_completo" to name,
                "cpf" to cpf,
                "data_de_nascimento" to birth,
                "celular" to celular
            )
            if(validate()) {
                db.collection("pessoas")
                    .add(pessoas)
                    .addOnSuccessListener { documentReference ->
                        Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding document", e)
                    }
                //caso precise associar um documento especifico
//                db.collection("pessoas").document(email)
//                    .set(pessoas)
//                    .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
//                    .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        auth.signOut()
                        Toast.makeText(this, "conta criada", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, SignInActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Log.e("error: ", it.exception.toString())
                    }
                }
            }
        }
        binding.btnLogin.setOnClickListener{
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }
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
        if (binding.etCel.text.toString().isEmpty()) {
            binding.textInputLayoutCelular.error = "é necessario preencher esse campo"
            return false
        } else if (binding.etCel.length() != 14) {
            binding.textInputLayoutCelular.error = "porfavor digite um celular valido"
            return false
        } else {
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

