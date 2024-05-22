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

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            if (NfcAdapter.ACTION_NDEF_DISCOVERED == it.action ||
                NfcAdapter.ACTION_TECH_DISCOVERED == it.action ||
                NfcAdapter.ACTION_TAG_DISCOVERED == it.action) {
                val navHostFragment =
                    supportFragmentManager.findFragmentById(R.id.navHostFragmentContainerView) as NavHostFragment
                val signInFragment = navHostFragment.childFragmentManager.fragments[0] as SignInFragment
                signInFragment.newIntent(it)
            }
        }
    }
}