package com.puc.pi3_es_2024_t24.manager

import android.app.Dialog
import android.app.PendingIntent
import android.content.Intent
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
import android.net.Uri
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.puc.pi3_es_2024_t24.databinding.DialogNfcBinding
import com.puc.pi3_es_2024_t24.databinding.FragmentConfirmLockerBinding
import com.puc.pi3_es_2024_t24.models.NfcTag
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ConfirmLockerFragment : Fragment() {
    private lateinit var binding:FragmentConfirmLockerBinding
    private lateinit var bindingNfc : DialogNfcBinding
    private var nfcAdapter: NfcAdapter? = null
    private lateinit var dialog:Dialog
    private lateinit var nfcTag: NfcTag
    private lateinit var clientId: String
    private lateinit var unityId : String
    private lateinit var time : Number
    private val db = Firebase.firestore
    private lateinit var armarioId : String
  
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentConfirmLockerBinding.inflate(inflater, container, false)
        val args = ConfirmLockerFragmentArgs.fromBundle(requireArguments())
        val argQr = args.qrInfo
        if (argQr != "null"){
            val json = argQr?.let { JSONObject(it) }
            clientId = json?.getString("clientId").toString()
            unityId = json?.getString("markerId").toString()
            val ti = json?.getString("payOption").toString()
            time = ti.toLong()
            fetchClientInfo(clientId)

        }
        bindingNfc = DialogNfcBinding.inflate(layoutInflater)

        nfcAdapter = NfcAdapter.getDefaultAdapter(requireContext())
        nfcTag = NfcTag("write")

        binding.btnEnd.setOnClickListener{
            showNfc()
        }


        return binding.root
    }
    private fun fetchClientInfo(clientId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            db.collection("pessoas").document(clientId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val name = document.getString("nome_completo")
                        val email = document.getString("email")
                        binding.etName.text = name
                        binding.etEmail.text = email
                        getPrice()
                    } else {
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle any errors
                }
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = ConfirmLockerFragmentArgs.fromBundle(requireArguments())
        val argUri1 = args.photoUri
        val argUri2 = args.photoUri1
        if (argUri1 != "noImg") {
            val igm1 = Uri.parse(argUri1)
            binding.img1.setImageURI(igm1)
            if (argUri2 != "noImg") {
                val igm2 = Uri.parse(argUri2)
                binding.img2.setImageURI(igm2)
                binding.img2.visibility = View.VISIBLE
            }
        }
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

        bindingNfc.tvNfc.text = nfcTag.method
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
                Log.d("TAG LIDA", "nfc tag detected")
                verificarTag(intent, tag)
                Log.d("TAG", "NFC Tag Detected")
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
                    val ndefRecord = NdefRecord.createMime(mimeType, """{"clientId": "$clientId", "locationId": "$armarioId"}""".toByteArray(Charsets.UTF_8))
                    val ndefMessage = NdefMessage(arrayOf(ndefRecord))
                    ndef.writeNdefMessage(ndefMessage)
                    ndef.close()
                    bindingNfc.tvNfc.text = "NFC ENCONTRADO. Escrevendo..."
                    navigateToSuccess()
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
                        Toast.makeText(requireContext(), "ID DO CLIENTE: $clientId", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
private fun navigateToSuccess(){
    val name = binding.etName.text
    val email = binding.etEmail.text
    val action = ConfirmLockerFragmentDirections.actionConfirmLockerFragmentToLocationSuccessFragment(
        name.toString(), email.toString()
    )
    findNavController().navigate(action)
}
    private fun getPrice() {
        CoroutineScope(Dispatchers.IO).launch {
            db.collection("unidades")
                .whereEqualTo("unityId", unityId)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        // Obter o campo 'precos' que é esperado ser um Map ou um Document
                        val precos = document.get("precos") as? Map<String, Any>

                        val preco = precos?.get("$time")

                        // Verifique se o valor não é nulo e faça algo com ele
                        if (preco != null) {
                            // Faça algo com o preço
                            registerCaucao(preco as Number, time as Long)
                        } else {
                            Log.d("Firestore", "Campo '$time' não encontrado em 'precos'")
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("Firestore", "Erro ao obter documentos: ", exception)
                }
        }
    }

    private fun registerCaucao(preco: Number, time: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            db.collection("armarios")
                .whereEqualTo("status", "disponivel")
                .whereEqualTo("unityId", unityId)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val docId = document.id

                        // Obter a hora atual e adicionar o tempo em minutos
                        val horaFinal = LocalDateTime.now().plusMinutes(time)
                        val horaFinalString = horaFinal.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

                        armarioId = docId
                        Toast.makeText(requireContext(), "armario $armarioId", Toast.LENGTH_SHORT).show()

                        // Atualizar o documento adicionando o campo "caucao" e alterando o "status"
                        db.collection("armarios").document(docId)
                            .update(
                                mapOf(
                                    "caucao" to preco,
                                    "status" to "ocupado",
                                    "horaFinal" to horaFinalString,
                                    "cliente" to clientId
                                )
                            )
                            .addOnSuccessListener {
                                Log.d("Firestore", "Documento atualizado com sucesso!")
                            }
                            .addOnFailureListener { e ->
                                Log.w("Firestore", "Erro ao atualizar documento", e)
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("Firestore", "Erro ao obter documentos: ", exception)
                }
        }
    }
}