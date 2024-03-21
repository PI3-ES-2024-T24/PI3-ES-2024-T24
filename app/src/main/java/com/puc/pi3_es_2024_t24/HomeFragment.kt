package com.puc.pi3_es_2024_t24

import android.os.Bundle
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
import com.puc.pi3_es_2024_t24.databinding.FragmentHomeBinding
import com.puc.pi3_es_2024_t24.databinding.FragmentSignInBinding

class HomeFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        val navController = findNavController()
        auth = Firebase.auth

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.bottom_credit_card ->Toast.makeText(requireContext(), "Fragmento pagamento!!!", Toast.LENGTH_SHORT).show()

                R.id.bottom_logout ->{
                    auth.signOut()
                    Toast.makeText(requireContext(), "Saiu da Conta!", Toast.LENGTH_SHORT).show()
                    navController.navigate(R.id.action_homeFragment_to_signInFragment)
                }
            }
            true
        }

        return binding.root
    }
}