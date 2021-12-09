package it.polito.ic2020.did2020.fiitapp

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.nutrizionista_pianoalimentare.*


class NutrizionistaPianoAlimentare : Fragment(R.layout.nutrizionista_pianoalimentare) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val dbData = listOf(
            ItemGiorno(0, getString(R.string.lun)),
            ItemGiorno(1, getString(R.string.mar)),
            ItemGiorno(2, getString(R.string.mer)),
            ItemGiorno(3, getString(R.string.gio)),
            ItemGiorno(4, getString(R.string.ven)),
            ItemGiorno(5, getString(R.string.sab)),
            ItemGiorno(6, getString(R.string.dom))
        )

        recyclerViewDiarioPaz.layoutManager = LinearLayoutManager(context)
        recyclerViewDiarioPaz.adapter = AdapterGiorniSettimana(dbData, true)

        topAppBar.setNavigationOnClickListener {
            Navigation.findNavController(view).navigateUp()
        }
    }
}