package com.puc.pi3_es_2024_t24.main

import android.app.Dialog
import android.app.PendingIntent
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.nfc.tech.NfcA
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.puc.pi3_es_2024_t24.R
import com.puc.pi3_es_2024_t24.databinding.DialogNfcBinding
import com.puc.pi3_es_2024_t24.databinding.DialogPaymentBinding
import com.puc.pi3_es_2024_t24.databinding.FragmentSignInBinding
import com.puc.pi3_es_2024_t24.models.NfcTag
import com.puc.pi3_es_2024_t24.models.QrCode
import org.json.JSONObject
import java.nio.charset.Charset

class SignInFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentSignInBinding
    private lateinit var db : FirebaseFirestore
    private var nfcAdapter: NfcAdapter? = null
    private lateinit var bindingNfc : DialogNfcBinding
    private lateinit var nfcTag : NfcTag
    private lateinit var qrCode : QrCode
    private lateinit var clientId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        val navController = findNavController()
        auth = Firebase.auth

        nfcAdapter = NfcAdapter.getDefaultAdapter(requireContext())
        nfcTag = NfcTag("", "")

        db = Firebase.firestore
        binding.btnSignIn.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            if (validate()) {
                //função do firebase auth para logar com email e senha
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val verifyemail = auth.currentUser?.isEmailVerified
                        if(verifyemail == true){
                            navController.navigate(R.id.action_signInFragment_to_nav_client)
                        }else{
                            Toast.makeText(requireContext(),"Conta não verificada, verfique no seu email",Toast.LENGTH_SHORT).show()
                        }
                    }else {
                        //cria um log do nivel E (error) no LogCat
                        binding.textInputLayoutEmail.error = "Email e/ou senha errados"
                        Log.e("error: ", it.exception.toString())
                    }
                }
            }
        }
        binding.btnMaps.setOnClickListener{
            it.findNavController().navigate(R.id.action_signInFragment_to_mapsFragment)
        }
        binding.btnRegister.setOnClickListener{
            it.findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }
        binding.forgotPassword.setOnClickListener {
            it.findNavController().navigate(R.id.action_signInFragment_to_forgotPasswordFragment)
        }
        binding.testCamera.setOnClickListener {
            it.findNavController().navigate(R.id.action_signInFragment_to_nav_manager)
        }
        binding.testNfc.setOnClickListener{
            nfcTag.method = "write"
            showNfc()
        }
        return binding.root
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

    override fun onResume() {
        super.onResume()
        val pendingIntent = PendingIntent.getActivity(
            requireContext(), 0,
            Intent(requireContext(), javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_MUTABLE)
        val intentFiltersArray = arrayOf(
            IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED).apply { addCategory(Intent.CATEGORY_DEFAULT) },
            IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED).apply { addCategory(Intent.CATEGORY_DEFAULT) },
            IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED).apply { addCategory(Intent.CATEGORY_DEFAULT) }
        )
        val techListArray = arrayOf(
            arrayOf(android.nfc.tech.Ndef::class.java.name)
        )
        nfcAdapter?.enableForegroundDispatch(requireActivity(), pendingIntent, intentFiltersArray, techListArray)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(requireActivity())
    }

    private fun showNfc() {
        if(!isAdded) return
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val bindingNfc = DialogNfcBinding.inflate(layoutInflater)
        dialog.setContentView(bindingNfc.root)

        bindingNfc.btnCloseNfc.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    fun newIntent(intent: Intent) {
        if(!isAdded) return
        if (NfcAdapter.ACTION_TAG_DISCOVERED == intent.action ||
            NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action ||
            NfcAdapter.ACTION_TECH_DISCOVERED == intent.action) {
            var tag : Tag? = null
            try {
                tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            } catch (e: Exception) {
                Log.d("NFC TAG ERROR", "${e.message}")
            }

            if (tag != null) {
                // Toast.makeText(requireContext(), "LEU TAG", Toast.LENGTH_SHORT).show()
                Log.d("TAG LIDA", "nfc tag detected")
                // verificarTag(intent, tag)
                Log.d("TAG", "NFC Tag Detected")
            }
        }
    }

    fun verificarTag(intent: Intent, tag: Tag) {
        if(!isAdded) return

        // VERIFICAR SE É PARA ESCREVER OU LER A TAG
        if (nfcTag.method == "write"){
            try {
                val ndef = Ndef.get(tag)
                if (ndef == null) {
                    Toast.makeText(requireContext(), "Nfc não suporta NDEF", Toast.LENGTH_SHORT).show()
                } else {
                    ndef.connect()
                    val mimeType = "text/plain"
                    val ndefRecord = NdefRecord.createMime(mimeType, """{"clientId": "${123123}"}""".toByteArray(Charsets.UTF_8))
                    val ndefMessage = NdefMessage(arrayOf(ndefRecord))
                    ndef.writeNdefMessage(ndefMessage)
                    ndef.close()
                    Log.d("WRITENFC", "NFC ESCRITO")
                    Toast.makeText(requireContext(), "TAG REGISTRADA!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.d("WriteNFC", "Erro ao tentar escrever no nfc!")
            }
        } else {
            intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)?.also {rawMessages ->
                val ndefMessages = rawMessages.map { it as NdefMessage}
                for (ndefMessage in  ndefMessages) {
                    for (record in ndefMessage.records) {
                        val payload = String(record.payload)
                        Log.d("TAG", "NDEF RECORD : $payload")
                        // instanciar o clientId com o dado recebido pelo nfc
                        clientId = JSONObject(payload).getString("clientId")
                    }
                }
            }
        }
    }
}