package com.puc.pi3_es_2024_t24.manager

import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.puc.pi3_es_2024_t24.R
import com.puc.pi3_es_2024_t24.databinding.FragmentMenuManagerBinding
import com.puc.pi3_es_2024_t24.models.NfcTag

class ManagerActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var nfcTag : NfcTag
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager)
        nfcTag = NfcTag("read")
    }
    // implementa a função de voltar com a seta de voltar do celular
    override fun onSupportNavigateUp(): Boolean {
        // Declara Main activity como Host de navegação
        navController= findNavController(R.id.navManagerFragmentContainerView)

        // retorna a pilha anterior
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            if (NfcAdapter.ACTION_NDEF_DISCOVERED == it.action ||
                NfcAdapter.ACTION_TECH_DISCOVERED == it.action ||
                NfcAdapter.ACTION_TAG_DISCOVERED == it.action) {
                val fragment = supportFragmentManager.findFragmentById(androidx.navigation.fragment.R.id.nav_host_fragment_container)
                fragment?.let {
                    when (it) {
                        is ConfirmLockerFragment -> it.newIntent(intent)
                        is MenuManagerFragment -> it.newIntent(intent)
                        else -> Log.d("NFC", "Fragmento desconhecido")
                    }
                }
            }
        }
    }

}