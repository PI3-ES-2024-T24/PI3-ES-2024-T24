package com.puc.pi3_es_2024_t24

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

        binding.btnSignUp.setOnClickListener{
            var email = binding.etEmail.text.toString()
            var password = binding.etPassword.text.toString()
            if(validate()) {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        auth.signOut()
                        Toast.makeText(this, "conta criada", Toast.LENGTH_SHORT).show()
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

