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
import kotlinx.android.synthetic.main.nutrizionista_pianodettaglio.*


class NutrizionistaPianoDettaglio : Fragment(R.layout.nutrizionista_pianodettaglio) {

    private var saved: Boolean = true
    private val fDatabase = FirebaseFirestore.getInstance()

    private val viewModel:CurrentPaziente by activityViewModels()

    private var txtGiorno = "Inserisci primo piano alimentare qui";

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

        when (arguments?.getInt("index")) {
            0 -> {txtGiorno = "lun"
                giorno.text = getString(R.string.lun) }
            1 -> {txtGiorno = "mar"
                giorno.text = getString(R.string.mar) }
            2 -> {txtGiorno = "mer"
                giorno.text = getString(R.string.mer) }
            3 -> {txtGiorno = "gio"
                giorno.text = getString(R.string.gio) }
            4 -> {txtGiorno = "ven"
                giorno.text = getString(R.string.ven) }
            5 -> {txtGiorno = "sab"
                giorno.text = getString(R.string.sab) }
            6 -> {txtGiorno = "dom"
                giorno.text = getString(R.string.dom) }
        }
        when (arguments?.getInt("index")) {
            0 -> giorno.text = "lun"
            1 -> txtGiorno = "mar"
            2 -> txtGiorno = "mer"
            3 -> txtGiorno = "gio"
            4 -> txtGiorno = "ven"
            5 -> txtGiorno = "sab"
            6 -> txtGiorno = "dom"
        }

        btn_save.setOnClickListener {
            updateDb(view, dbPaziente)
        }

        dbPaziente.get().addOnCompleteListener { docPazienti ->
            val pianoGior = docPazienti.result?.get("piano_al.$txtGiorno") as List<String>
            edit_colazione.editText?.setText(pianoGior[0])
            edit_pranzo.editText?.setText(pianoGior[1])
            edit_cena.editText?.setText(pianoGior[2])
            edit_spuntino.editText?.setText(pianoGior[3])

            edit_colazione.editText?.doAfterTextChanged { saved = false }
            edit_pranzo.editText?.doAfterTextChanged { saved = false }
            edit_cena.editText?.doAfterTextChanged { saved = false }
            edit_spuntino.editText?.doAfterTextChanged { saved = false }
        }
    }

    private fun updateDb(view: View, db: DocumentReference){
        val pianoGior = listOf(
                edit_colazione.editText?.text.toString(),
                edit_pranzo.editText?.text.toString(),
                edit_cena.editText?.text.toString(),
                edit_spuntino.editText?.text.toString())
        db.update("piano_al.$txtGiorno", pianoGior)
                .addOnSuccessListener {
                    saved = true
                    db.update("piano_al.isUpdated", false)
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