package it.polito.ic2020.did2020.fiitapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.paziente_diariosettimanale.*

class PazienteDiarioSettimanale : Fragment(R.layout.paziente_diariosettimanale) {

    private val fDatabase = FirebaseFirestore.getInstance()
    private val _dbData = mutableListOf<ItemDiario>()
    val fAuth = FirebaseAuth.getInstance()
    val fUser = fAuth.currentUser.uid


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val dbPaziente = fDatabase.collection("pazienti").document(fUser)

        topAppBar.setNavigationOnClickListener {
            Navigation.findNavController(view).navigateUp()
        }

       dbPaziente.get().addOnCompleteListener { docPazienti ->
           val diarioSett = docPazienti.result?.get("diario_sett") as Map<*,*>
           val diarioTesto = diarioSett["testo"] as List<*>
           val diarioDate = diarioSett["data"] as List<*>
            for (i in diarioTesto.indices) {
                _dbData.add(ItemDiario(i, diarioTesto[i].toString(), diarioDate[i].toString()))
                Log.d("dati",_dbData.toString())
            }
            dbPaziente.update("diario_sett.isUpdated", false)
            recyclerViewDiarioPaz.layoutManager = LinearLayoutManager(context)
            recyclerViewDiarioPaz.adapter = AdapterDiario(_dbData)
        }

        btn_inviaDiario.setOnClickListener{
            val testoInserito = diarioTxt.text.toString()
            dbPaziente.get().addOnCompleteListener{ doc ->
                val testi: MutableList<String> = doc.result?.get("diario_sett.testo") as MutableList<String>
                val date: MutableList<String> = doc.result?.get("diario_sett.data") as MutableList<String>
                testi.add(0, testoInserito)
                date.add(0, System.currentTimeMillis().toString())
                dbPaziente.update("diario_sett.testo", testi)
                dbPaziente.update("diario_sett.data", date)

                val toast = Toast.makeText(context, getString(R.string.toast_save), Toast.LENGTH_LONG)
                toast.show()
                Navigation.findNavController(view).navigateUp()
            }.addOnFailureListener {
                val toast = Toast.makeText(context, getString(R.string.toast_error), Toast.LENGTH_LONG)
                toast.show()
                Navigation.findNavController(view).navigateUp()
            }
        }

    }




}