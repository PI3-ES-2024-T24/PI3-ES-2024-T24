package com.puc.pi3_es_2024_t24.manager

import android.content.Intent
import android.app.Dialog
import android.app.PendingIntent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.puc.pi3_es_2024_t24.R
import com.puc.pi3_es_2024_t24.databinding.DialogNfcBinding
import com.puc.pi3_es_2024_t24.databinding.FragmentLocationSuccessBinding
import com.puc.pi3_es_2024_t24.databinding.FragmentMenuManagerBinding
import com.puc.pi3_es_2024_t24.main.MainActivity
import com.puc.pi3_es_2024_t24.models.NfcTag
import org.json.JSONObject

class MenuManagerFragment : Fragment() {
    private lateinit var auth: FirebaseAuth

    private lateinit var binding: FragmentMenuManagerBinding
    private lateinit var nfcTag: NfcTag
    private lateinit var dialog: Dialog
    private lateinit var clientId: String
    private lateinit var bindingNfc : DialogNfcBinding
    private var nfcAdapter: NfcAdapter? = null

    private val db = Firebase.firestore


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        auth = Firebase.auth
        binding = FragmentMenuManagerBinding.inflate(inflater, container, false)

        nfcAdapter = NfcAdapter.getDefaultAdapter(requireContext())
        nfcTag = NfcTag("")

        binding.btnReleaseLocker.setOnClickListener {
            nfcTag.method= "write"
            findNavController().navigate(R.id.action_menuManagerFragment_to_cameraFragment)
        }
        binding.btnOpenLocker.setOnClickListener {
            nfcTag.method = "read"
            showNfc()
        }

        binding.btnLogOutManager.setOnClickListener {
            val intent = Intent(requireContext(),MainActivity::class.java)
            startActivity(intent)
            auth.signOut()
            requireActivity().finish()
        }
        return binding.root
    }
    override fun onResume() {
        super.onResume()
        val pendingIntent = PendingIntent.getActivity(
            requireContext(), 0,
            Intent(requireContext(), javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_MUTABLE
        )
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
        if (!isAdded) return
        dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        bindingNfc = DialogNfcBinding.inflate(layoutInflater)
        dialog.setContentView(bindingNfc.root)

        bindingNfc.btnCloseNfc.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    fun newIntent(intent: Intent) {
        if (!isAdded) return
        if (NfcAdapter.ACTION_TAG_DISCOVERED == intent.action ||
            NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action ||
            NfcAdapter.ACTION_TECH_DISCOVERED == intent.action) {
            var tag: Tag? = null
            try {
                tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            } catch (e: Exception) {
                Log.d("NFC TAG ERROR", "${e.message}")
            }

            if (tag != null) {
                verificarTag(intent, tag)
            }
        }
    }

    private fun verificarTag(intent: Intent, tag: Tag) {
        if (!isAdded) return

        if (nfcTag.method == "write") {
            try {
                val ndef = Ndef.get(tag)
                if (ndef == null) {
                    Log.d("NfcError", "NFC NÃO SUPORTA NDEF")
                } else {
                    ndef.connect()
                    val mimeType = "text/plain"
                    val ndefRecord = NdefRecord.createMime(mimeType, """{"clientId": "${123123}"}""".toByteArray(Charsets.UTF_8))
                    val ndefMessage = NdefMessage(arrayOf(ndefRecord))
                    ndef.writeNdefMessage(ndefMessage)
                    ndef.close()
                    bindingNfc.tvNfc.text = "NFC ENCONTRADO. Escrevendo..."
                    Toast.makeText(requireContext(), "NFC registrado com sucesso!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_confirmLockerFragment_to_locationSuccessFragment)
                    dialog.dismiss()

                }
            } catch (e: Exception) {
                Log.d("WriteNFC", "Erro ao tentar escrever no nfc!")
            }
        } else {
            intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)?.also { rawMessages ->
                val ndefMessages = rawMessages.map { it as NdefMessage }
                for (ndefMessage in ndefMessages) {
                    for (record in ndefMessage.records) {
                        val payload = String(record.payload)
                        Log.d("TAG", "NDEF RECORD : $payload")
                        clientId = JSONObject(payload).getString("clientId")
                        bindingNfc.tvNfc.text = "NFC ENCONTRADO : $clientId"
                        ReleaseLockerFragment().loadClientInfo(clientId)
                        findNavController().navigate(R.id.action_menuManagerFragment_to_releaseLockerFragment)
                    }
                }
            }
        }

    }
}