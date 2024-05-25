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
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.puc.pi3_es_2024_t24.R
import com.puc.pi3_es_2024_t24.databinding.DialogCloseBinding
import com.puc.pi3_es_2024_t24.databinding.DialogNfcBinding
import com.puc.pi3_es_2024_t24.databinding.DialogReleaseBinding
import com.puc.pi3_es_2024_t24.databinding.FragmentMenuManagerBinding
import com.puc.pi3_es_2024_t24.main.MainActivity
import com.puc.pi3_es_2024_t24.models.NfcTag
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class MenuManagerFragment : Fragment() {
    private lateinit var auth: FirebaseAuth

    private lateinit var binding: FragmentMenuManagerBinding
    private lateinit var nfcTag: NfcTag
    private lateinit var dialog: Dialog
    private lateinit var clientId: String
    private lateinit var clientName: String
    private lateinit var bindingNfc : DialogNfcBinding
    private lateinit var bindingRelease : DialogReleaseBinding
    private lateinit var bindingClose : DialogCloseBinding
    private lateinit var armarioId : String
    private var nfcAdapter: NfcAdapter? = null
    private lateinit var novoCaucao : Number
    private lateinit var dialogRelease:Dialog
    private lateinit var dialogClose:Dialog
    private val imageUrls = mutableListOf<String>()
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
            auth.signOut()
            CoroutineScope(Dispatchers.IO).launch {
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
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
                    val ndefRecord = NdefRecord.createMime(mimeType, """{"clientId": ""}""".toByteArray(Charsets.UTF_8))
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
                        if (clientId == "") {
                            Toast.makeText(requireContext(), "NFC vazia!", Toast.LENGTH_SHORT).show()
                            return
                        }
                        armarioId = JSONObject(payload).getString("locationId")
                        loadLocker()
                        loadClientInfo(clientId)
                        dialog.dismiss()
                        bindingNfc.tvNfc.text = "NFC ENCONTRADO : $clientId"
                        if (clientId != "") {
                            loadClientInfo(clientId)
                            dialog.dismiss()
                        } else {
                            Toast.makeText(requireContext(), "NFC vazia!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

    }
    private fun loadClientInfo(clientId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                db.collection("pessoas")
                    .document(clientId)
                    .get()
                    .addOnSuccessListener { document ->
                        clientName = document.getString("nome_completo").toString()
                        releaseLockerDialog()
                    }
            } catch (e: Exception) {
                Log.d("LoadClient", "${e.message}")
            }
        }
    }

    private fun releaseLockerDialog() {
        if (!isAdded) return
        dialogRelease = Dialog(requireContext())
        dialogRelease.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogRelease.setCancelable(false)
        dialogRelease.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        bindingRelease = DialogReleaseBinding.inflate(layoutInflater)
        dialogRelease.setContentView(bindingRelease.root)

        bindingRelease.btnOpen.setOnClickListener {
            Toast.makeText(requireContext(), "Armário aberto!", Toast.LENGTH_SHORT).show()
            // ABRIR MOMENTANEAMENTE
        }

        bindingRelease.btnClose.setOnClickListener {
            closeLockerDialog()
            dialogRelease.dismiss()
            // ABRIR NOVO DIALOG DE ENCERRAR LOCAÇÃO

        }

        bindingRelease.btnBack.setOnClickListener {
            dialogRelease.dismiss()
        }

        dialogRelease.show()
    }

    private fun closeLockerDialog() {
        if (!isAdded) return
        dialogClose = Dialog(requireContext())
        dialogClose.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogClose.setCancelable(false)
        dialogClose.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        bindingClose = DialogCloseBinding.inflate(layoutInflater)
        dialogClose.setContentView(bindingClose.root)

        bindingClose.btnClose.setOnClickListener {
            // ENCERRAR LOCAÇÃO
            closeLocker(armarioId)
        }

        bindingClose.btnBack.setOnClickListener {
            dialogClose.dismiss()
            dialogRelease.show()
        }

        dialogClose.show()
    }

    private fun closeLocker(armarioId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            db.collection("armarios").document(armarioId).get()
                .addOnSuccessListener { document ->
                    val horaFinalString = document.getString("horaFinal")
                    val caucao = document.getDouble("caucao")
                    if (horaFinalString != null && caucao != null) {
                        val horaFinal = LocalDateTime.parse(horaFinalString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        val agora = LocalDateTime.now()

                        val minutosDiferenca = ChronoUnit.MINUTES.between(agora, horaFinal)

                        novoCaucao = if (minutosDiferenca < 0) {
                            // Se o horaFinal for menor que agora, calcule a penalidade
                            caucao + minutosDiferenca // minutosDiferenca será negativo
                        } else {
                            if (minutosDiferenca > 0) {
                                caucao - minutosDiferenca
                            } else {
                                caucao
                            }
                        }

                        // Atualizar os campos do documento
                        db.collection("armarios").document(armarioId)
                            .update(
                                mapOf(
                                    "caucao" to 0,
                                    "cliente" to "",
                                    "horaFinal" to "",
                                    "status" to "disponivel"
                                )
                            )
                            .addOnSuccessListener {
                                nfcTag.method = "write"
                                dialog.show()
                                dialogClose.dismiss()
                                dialogRelease.dismiss()
                                Toast.makeText(requireContext(), "VALOR A SER PAGO : $novoCaucao. Se o valor for menor que a caução é necessário devolver tal" +
                                        "senão é necessário receber tal valor de diferença.", Toast.LENGTH_SHORT).show()
                                Log.d("Firestore", "Documento atualizado com sucesso!")
                            }
                            .addOnFailureListener { e ->
                                Log.w("Firestore", "Erro ao atualizar documento", e)
                            }
                    } else {
                        Log.w("Firestore", "Documento não contém os campos necessários")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("Firestore", "Erro ao obter documento: ", exception)
                }
        }
    }

    private fun loadLocker() {
        CoroutineScope(Dispatchers.IO).launch {
            db.collection("armarios").document(armarioId).get()
                .addOnSuccessListener { document ->
                    // Obter o campo 'images' como uma lista de strings
                    val images = document.get("images") as? List<String>

                    images?.let {
                        if (it.isNotEmpty()) {
                            Glide.with(requireContext())
                                .load(it[0])
                                .into(bindingRelease.ivPhoto)
                            Glide.with(requireContext())
                                .load(it[0])
                                .into(bindingClose.ivPhoto)
                        }
                        if (it.size > 1) {
                            Glide.with(requireContext())
                                .load(it[1])
                                .into(bindingRelease.ivPhoto2)
                            Glide.with(requireContext())
                                .load(it[1])
                                .into(bindingClose.ivPhoto2)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("Firestore", "Erro ao obter documento: ", exception)
                }
        }
    }
}