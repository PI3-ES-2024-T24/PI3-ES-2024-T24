package com.puc.pi3_es_2024_t24.manager

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.puc.pi3_es_2024_t24.R
import com.puc.pi3_es_2024_t24.databinding.FragmentConfirmLockerBinding
import com.puc.pi3_es_2024_t24.databinding.FragmentQrCodeReadBinding
import org.json.JSONObject

class ConfirmLockerFragment : Fragment() {
private lateinit var binding:FragmentConfirmLockerBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentConfirmLockerBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = ConfirmLockerFragmentArgs.fromBundle(requireArguments())
        val argUri1 = args.photoUri
        val argUri2 = args.photoUri1
        if (argUri1 != "noImg"){
            val igm1 = Uri.parse(argUri1)
            binding.img1.setImageURI(igm1)
            if (argUri2 != "noImg"){
                val igm2 = Uri.parse(argUri2)
                binding.img2.setImageURI(igm2)
                binding.img2.visibility = View.VISIBLE
            }
        }

    }

}