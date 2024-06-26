package com.puc.pi3_es_2024_t24.main

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.puc.pi3_es_2024_t24.R
import com.puc.pi3_es_2024_t24.manager.ConfirmLockerFragment

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
// implementa a função de voltar com a seta de voltar do celular
    override fun onSupportNavigateUp(): Boolean {
        // Declara Main activity como Host de navegação
        navController= findNavController(R.id.navHostFragmentContainerView)
    // retorna a pilha anterior
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}