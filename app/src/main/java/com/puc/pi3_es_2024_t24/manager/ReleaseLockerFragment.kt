package com.puc.pi3_es_2024_t24.manager

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.puc.pi3_es_2024_t24.R
import com.puc.pi3_es_2024_t24.databinding.FragmentReleaseLockerBinding
import com.puc.pi3_es_2024_t24.models.Card
import com.puc.pi3_es_2024_t24.models.Client
import com.puc.pi3_es_2024_t24.models.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReleaseLockerFragment : Fragment() {

    private lateinit var binding : FragmentReleaseLockerBinding
    private val db = Firebase.firestore
    private val sharedViewModel: SharedViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentReleaseLockerBinding.inflate(inflater, container, false)

        binding.btnOpen.setOnClickListener{
            Toast.makeText(requireContext(), "Armário aberto!", Toast.LENGTH_SHORT).show()
        }

        binding.btnClose.setOnClickListener {
            findNavController().navigate(R.id.action_releaseLockerFragment_to_closeLocationFragment)
        }

        return binding.root
    }

    fun loadClientInfo(clientId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                db.collection("pessoas")
                    .document(clientId)
                    .get()
                    .addOnSuccessListener { document ->
                        val clientName = document.getString("nome").toString()
                        sharedViewModel.setClientName(clientName)
                        binding.tvUserName.text = clientName
                    }
            } catch (e: Exception) {
                Log.d("LoadClient", "${e.message}")
            }
        }

    }
}