package it.polito.ic2020.did2020.fiitapp

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.nutrizionista_dettaglio_paziente.*
import java.io.File


class NutrizionistaDettaglioPaziente : Fragment(R.layout.nutrizionista_dettaglio_paziente) {

    private val viewModel:CurrentPaziente by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        topAppBar.setNavigationOnClickListener {
            Navigation.findNavController(view).navigateUp()
        }

        dettaglioP_btn.setOnClickListener {
            findNavController().navigate(R.id.action_nutrizionista_dettaglio_paziente_to_pesatura)
        }
        btn_pianoAlimentare.setOnClickListener {
            findNavController().navigate(R.id.action_nutrizionista_dettaglio_paziente_to_nutrizionista_pianoalimentare)
        }
        btn_diarioSettimanale.setOnClickListener {
            findNavController().navigate(R.id.action_nutrizionista_dettaglio_paziente_to_nutrizionista_diariosettimanale)
        }
        btn_obiettivoMensile.setOnClickListener {
            findNavController().navigate(R.id.action_nutrizionista_dettaglio_paziente_to_nutrizionista_obiettivomensile)
        }

        loadProfile()

    }

    private fun loadProfile() {
        val idUtente = viewModel.getId()
        val fDatabase = FirebaseFirestore.getInstance()
        val dbUsers = fDatabase.collection("users").document(idUtente)
        val dbPaziente = fDatabase.collection("pazienti").document(idUtente)

        val imageName = idUtente
        val fStorage = FirebaseStorage.getInstance().reference.child("image/$imageName.jpg")
        val localfile = File.createTempFile("tempImage","jpg")

        fStorage.getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            item_fotoDettaglioPaziente.setImageBitmap(bitmap)
        }

        dbUsers.get().addOnCompleteListener { docUsers ->
            dbPaziente.get().addOnCompleteListener { docPazienti ->
                nomeUtente.text = docUsers.result?.get("Nome").toString().plus(" ").plus(docUsers.result?.get("Cognome").toString())
                val pesi:List<Float>? = docPazienti.result?.get("pesatura.peso") as List<Float>?
                if (pesi != null) {
                    if (pesi.isNotEmpty() ) {
                        lastPesoPaziente.text = pesi[0].toString().plus("kg")
                    }
                }
                if(!(docPazienti.result?.get("pesatura.isUpdated") as Boolean)) {
                    notifyPeso.visibility = View.VISIBLE
                }
                if(!(docPazienti.result?.get("diario_sett.isUpdated") as Boolean)) {
                    notifyDiario.visibility = View.VISIBLE
                }
            }
        }
    }

}