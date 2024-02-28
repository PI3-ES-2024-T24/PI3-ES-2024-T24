package com.puc.pi3_es_2024_t24

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.puc.pi3_es_2024_t24.databinding.ActivityHomeBinding
import com.puc.pi3_es_2024_t24.databinding.ActivitySignInBinding
import com.puc.pi3_es_2024_t24.databinding.ActivitySignUpBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.btnLogOut.setOnClickListener{
            auth.signOut()
            Toast.makeText(this, "saiu da conta", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}