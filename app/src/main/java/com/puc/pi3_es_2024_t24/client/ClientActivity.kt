package com.puc.pi3_es_2024_t24.client

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.puc.pi3_es_2024_t24.R

class ClientActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client)
    }
    // implementa a função de voltar com a seta de voltar do celular
    override fun onSupportNavigateUp(): Boolean {
        // Declara Main activity como Host de navegação
        navController= findNavController(R.id.navClientFragmentContainerView)
        // retorna a pilha anterior
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}