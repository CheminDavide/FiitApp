package it.polito.ic2020.did2020.fiitapp

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.registrazione1.*


class Registrazione1 : Fragment(R.layout.registrazione1) {

    private var isNutrizionista: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        scelta_PN.setOnCheckedChangeListener{ _, checkedId ->
            if(checkedId == R.id.radioBtn_paziente){
                cod_nutrizionista.isEnabled = true
                cod_nutrizionista.isVisible = true
                cod_nutrizionista.hint = "Inserisci il Codice fornito dalla Nutrizionista"
            }else if(checkedId == R.id.radioBtn_nutrizionista) {
                cod_nutrizionista.isVisible = false
                cod_nutrizionista.hint = ""
            }
        }
        

        continua_registrazione.setOnClickListener{

            val checkedId: Int = scelta_PN!!.checkedRadioButtonId

            if (checkedId == R.id.radioBtn_paziente) {
                isNutrizionista = false
                val codice: String = cod_nutrizionista.text.toString().trim()
                if (TextUtils.isEmpty(codice)) {
                    cod_nutrizionista.error = "Il campo CODICE NUTRIZIONISTA è obbligatorio"
                }else{
                    val direction = Registrazione1Directions.actionRegistrazione1ToRegistrazione(codice, isNutrizionista)
                    findNavController().navigate(direction)
                }
            } else if(checkedId == R.id.radioBtn_nutrizionista) {
                isNutrizionista = true
                val direction = Registrazione1Directions.actionRegistrazione1ToRegistrazione("none",isNutrizionista)
                findNavController().navigate(direction)
            }

        }
        
        toolbar2.setOnClickListener {
            findNavController().navigate(R.id.action_registrazione1_to_frgmt_main)
        }

    }
}


