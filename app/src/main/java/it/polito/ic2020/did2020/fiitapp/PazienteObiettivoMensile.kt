package it.polito.ic2020.did2020.fiitapp

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.paziente_obiettivomensile.*
import kotlin.math.roundToInt

class PazienteObiettivoMensile : Fragment(R.layout.paziente_obiettivomensile) {

    private val fDatabase = FirebaseFirestore.getInstance()
    private val fAuth = FirebaseAuth.getInstance()
    private val dbPaziente = fDatabase.collection("pazienti").document(fAuth.currentUser.uid)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        topAppBar.setNavigationOnClickListener {
            Navigation.findNavController(view).navigateUp()
        }

        dbPaziente.get().addOnCompleteListener { docPazienti ->
            val obiettivo = docPazienti.result?.get("ob_mensile") as Map<*, *>
            val peso = docPazienti.result?.get("pesatura.peso") as List<Float>

            if(obiettivo["peso"].toString().isNotEmpty() && peso.isNotEmpty()) {
                val kg = obiettivo["peso"].toString()
                stats_progressbar.progress = (kg.toFloat() * 100.0 / peso[0]).roundToInt()
                pesoObiettivo.text = kg.plus(" kg")
                pesoPerdere.text = (peso[0] - kg.toFloat()).toString().plus(" kg")
                pesoPerso.text = (peso.last() - peso[0]).toString().plus(" kg")
            } else {
                pesoObiettivo.text = "0.0 kg"
            }

            if(obiettivo["testo"].toString().isNotEmpty()) {
                testoObiettivo.text = obiettivo["testo"].toString()
            } else {
                testoObiettivo.text = "Nessun inserimento"
            }

            dbPaziente.update("ob_mensile.isUpdated", true)
        }
    }
}