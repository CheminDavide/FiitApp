package it.polito.ic2020.did2020.fiitapp

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.nutrizionista_diariosettimanale.*
import kotlinx.android.synthetic.main.nutrizionista_diariosettimanale.noInserimento
import kotlinx.android.synthetic.main.nutrizionista_diariosettimanale.topAppBar


class NutrizionistaDiarioSettimanale : Fragment(R.layout.nutrizionista_diariosettimanale) {

    private val fDatabase = FirebaseFirestore.getInstance()

    private val viewModel:CurrentPaziente by activityViewModels()

    private val _dbData = mutableListOf<ItemDiario>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val idUtente = viewModel.getId()
        val dbPaziente = fDatabase.collection("pazienti").document(idUtente)

        topAppBar.setNavigationOnClickListener {
            Navigation.findNavController(view).navigateUp()
        }

        dbPaziente.get().addOnCompleteListener { docPazienti ->
            val diarioSett = docPazienti.result?.get("diario_sett") as Map<*,*>
            val diarioTesto = diarioSett["testo"] as List<*>
            val diarioDate = diarioSett["data"] as List<*>

            if(diarioTesto.isNotEmpty()) {
                for (i in diarioTesto.indices) {
                    _dbData.add(ItemDiario(i, diarioTesto[i].toString(), diarioDate[i].toString()))
                }
                recyclerViewDiarioPaz.layoutManager = LinearLayoutManager(context)
                recyclerViewDiarioPaz.adapter = AdapterDiario(_dbData)
            } else {
                noInserimento.visibility = View.VISIBLE
            }

            dbPaziente.update("diario_sett.isUpdated", true)
        }

    }

}