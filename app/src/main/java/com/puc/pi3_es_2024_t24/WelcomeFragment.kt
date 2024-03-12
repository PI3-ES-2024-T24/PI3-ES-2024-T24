package com.puc.pi3_es_2024_t24

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.puc.pi3_es_2024_t24.databinding.FragmentWelcomeBinding

class WelcomeFragment : Fragment() {

    private lateinit var binding:FragmentWelcomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentWelcomeBinding.inflate(inflater, container, false)

        (activity as? AppCompatActivity)?.supportActionBar?.hide()

        val navController = findNavController()

        navController.let {  // Safe navigation using let
            Handler(Looper.getMainLooper()).postDelayed({
                it.navigate(R.id.action_welcomeFragment_to_signInFragment)
            }, 2000)
        }
        return binding.root

    }
}