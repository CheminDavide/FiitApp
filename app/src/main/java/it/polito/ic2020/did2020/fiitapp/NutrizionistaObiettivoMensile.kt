package it.polito.ic2020.did2020.fiitapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.nutrizionista_obiettivomensile.*


class NutrizionistaObiettivoMensile : Fragment(R.layout.nutrizionista_obiettivomensile) {

    private var saved: Boolean = true

    private val fDatabase = FirebaseFirestore.getInstance()

    private val viewModel:CurrentPaziente by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val idUtente = viewModel.getId()
        val dbPaziente = fDatabase.collection("pazienti").document(idUtente)

        topAppBar.inflateMenu(R.menu.save_appbar)

        topAppBar.setNavigationOnClickListener {
            if(!saved) {
                showDialog(view, dbPaziente)
            } else {
                Navigation.findNavController(view).navigateUp()
            }
        }

        topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.salva -> {
                    updateDb(view, dbPaziente)
                    true
                }

                else -> {
                    super.onOptionsItemSelected(it)
                }
            }
        }

        btn_save.setOnClickListener {
            updateDb(view, dbPaziente)
        }

        dbPaziente.get().addOnCompleteListener { docPazienti ->
            val obiettivo = docPazienti.result?.get("ob_mensile") as Map<*, *>
            edit_ob_peso.editText?.setText(obiettivo["peso"].toString())
            edit_ob_mese.editText?.setText(obiettivo["testo"].toString())

            edit_ob_peso.editText?.doAfterTextChanged { saved = false }
            edit_ob_mese.editText?.doAfterTextChanged { saved = false }
        }


    }

    private fun updateDb(view: View, db: DocumentReference){
        val obiettivo = mapOf(
            "isUpdated" to false,
            "peso" to edit_ob_peso.editText?.text.toString().toFloat(),
            "testo" to edit_ob_mese.editText?.text.toString())
        db.update("ob_mensile", obiettivo)
            .addOnSuccessListener {
                saved = true
                val toast = Toast.makeText(context, getString(R.string.toast_save), Toast.LENGTH_LONG)
                toast.show()
                Navigation.findNavController(view).navigateUp()
            }
            .addOnFailureListener {
                val toast = Toast.makeText(context, getString(R.string.toast_error), Toast.LENGTH_LONG)
                toast.show()
                Navigation.findNavController(view).navigateUp()
            }
    }

    private fun showDialog (view: View, db: DocumentReference) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setMessage(R.string.dialog_messaggio)
                .setTitle(R.string.dialog_titolo)
                .setPositiveButton(R.string.salva) { _, _ ->
                    updateDb(view, db)
                }
                .setNegativeButton(R.string.no) { _, _ ->
                    Navigation.findNavController(view).navigateUp()
                }
        builder.create()
        builder.show()
    }
}