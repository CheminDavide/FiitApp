package it.polito.ic2020.did2020.fiitapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.paziente_pianodettaglio.*


class PazientePianoDettaglio : Fragment(R.layout.paziente_pianodettaglio) {

    private val fDatabase = FirebaseFirestore.getInstance()
    private val fAuth = FirebaseAuth.getInstance()
    private val dbPaziente = fDatabase.collection("pazienti").document(fAuth.currentUser.uid)

    private var txtGiorno = ""
    private var noDb = "Nessun inserimento"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        topAppBar.setNavigationOnClickListener {
            Navigation.findNavController(view).navigateUp()
        }

        when (arguments?.getInt("index")) {
            0 -> txtGiorno = "lun"
            1 -> txtGiorno = "mar"
            2 -> txtGiorno = "mer"
            3 -> txtGiorno = "gio"
            4 -> txtGiorno = "ven"
            5 -> txtGiorno = "sab"
            6 -> txtGiorno = "dom"
        }
        dbPaziente.get().addOnCompleteListener { docPazienti ->
            val pianoGior = docPazienti.result?.get("piano_al.$txtGiorno") as List<String>

            if(pianoGior[0].isNotEmpty()) {
                colazione.text = pianoGior[0]
            } else {
                colazione.text = noDb
            }

            if(pianoGior[1].isNotEmpty()) {
                pranzo.text = pianoGior[1]
            } else {
                pranzo.text = noDb
            }

            if(pianoGior[2].isNotEmpty()) {
                cena.text = pianoGior[2]
            } else {
                cena.text = noDb
            }

            if(pianoGior[3].isNotEmpty()) {
                spuntino.text = pianoGior[3]
            } else {
                spuntino.text = noDb
            }

            dbPaziente.update("piano_al.isUpdated", true)
        }
    }
}