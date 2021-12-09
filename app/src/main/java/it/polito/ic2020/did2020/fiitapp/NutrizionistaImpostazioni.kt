package it.polito.ic2020.did2020.fiitapp

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.nutrizionista_impostazioni.*


class NutrizionistaImpostazioni: Fragment(R.layout.nutrizionista_impostazioni) {

    val user = Firebase.auth.currentUser
    val fDatabase = FirebaseFirestore.getInstance()
    val dbUsers = fDatabase.collection("users").document(user.uid)

    private var saved: Boolean = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val SalvaP: Button = SalvaPass
        val SalvaM: Button = SalvaMail
        val logout: Button = Logout
        val delete: Button = CancellaAccount


        loadData()

        TopAppBar_imp.setNavigationOnClickListener {
            if(!saved) {
                showDialog(view)
            } else {
                Navigation.findNavController(view).navigateUp()
            }
        }


        logout.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            findNavController().navigate(R.id.action_nutrizionista_impostazioni_to_frgmt_main)
        }

        delete.setOnClickListener{
            val dialogBuilder= AlertDialog.Builder(requireActivity())
            dialogBuilder.setMessage("Sei sicuro di voler eliminare il tuo profilo?").setPositiveButton("Si",DialogInterface.OnClickListener{
                    _, _ ->  user.delete().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Utente Eliminato")
                    findNavController().navigate(R.id.action_nutrizionista_impostazioni_to_frgmt_main)
                }
            }
            }).setNeutralButton("Annulla") { dialogBuilder, _ -> dialogBuilder.dismiss() }

            val alert = dialogBuilder.create()
            alert.setTitle("Cancella Profilo")
            alert.show()
        }



        SalvaM.setOnClickListener{
            if(MailEdit.text.toString().trim().isNotEmpty()){
                user!!.updateEmail(MailEdit.text.toString().trim()).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        dbUsers.update("Email",MailEdit.text.toString().trim())
                        Toast.makeText(context, "Mail aggiornata", Toast.LENGTH_LONG).show()
                    }
                }
            }

        }
        SalvaP.setOnClickListener{
            if(PassEdit.toString().trim().compareTo(PassEditConfirm.text.toString().trim())==0 && PassEdit.text.toString().isNotEmpty() && PassEditConfirm.text.toString().isNotEmpty()) {
                user.updatePassword(PassEditConfirm.text.toString().trim()).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        dbUsers.update("Password", PassEditConfirm.text.toString().trim())
                        Toast.makeText(context, "Password aggiornata", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }


    }

    private fun loadData() {
        dbUsers.get().addOnCompleteListener { docUsers ->
            MailEdit.hint = docUsers.result?.get("Email").toString()
            MailEdit.doAfterTextChanged { saved = false }
            PassEdit?.doAfterTextChanged { saved = false }
        }
    }

    private fun showDialog (view: View) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireActivity())
        builder.setMessage(R.string.dialog_messaggio)
            .setTitle(R.string.dialog_titolo)
            .setPositiveButton(R.string.indietro) { dialogBuilder, _ -> dialogBuilder.dismiss() }
            .setNegativeButton(R.string.no) { _, _ ->
                Navigation.findNavController(view).navigateUp()
            }
        builder.create()
        builder.show()
    }
}