package it.polito.ic2020.did2020.fiitapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.nutrizionista_pesatura.*

class NutrizionistaPesatura : Fragment(R.layout.nutrizionista_pesatura) {

    private val fDatabase = FirebaseFirestore.getInstance()

    private val viewModel:CurrentPaziente by activityViewModels()

    private val _dbData = mutableListOf<ItemPeso>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val idUtente = viewModel.getId()
        val dbPaziente = fDatabase.collection("pazienti").document(idUtente)

        topAppBar.setNavigationOnClickListener {
            Navigation.findNavController(view).navigateUp()
        }

        dbPaziente.get().addOnCompleteListener { docPazienti ->
            val pesatura = docPazienti.result?.get("pesatura") as Map<*,*>
            val peso = pesatura["peso"] as List<Float>
            val pesoData = pesatura["data"] as List<*>
            for (i in peso.indices) {
                _dbData.add(ItemPeso(peso[i], pesoData[i].toString()))
            }

            if(peso.isNotEmpty()) {
                textUltimoPeso.text = peso[0].toString().plus(" kg")

                listaPesi.layoutManager = LinearLayoutManager(context)
                listaPesi.adapter = AdapterPesi(_dbData)
            } else {
                noInserimento.visibility = View.VISIBLE
            }

            dbPaziente.update("pesatura.isUpdated", true)
        }

    }
}