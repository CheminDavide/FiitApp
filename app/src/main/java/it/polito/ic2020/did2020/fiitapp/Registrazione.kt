package it.polito.ic2020.did2020.fiitapp

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.registrazione.*
import kotlinx.android.synthetic.main.registrazione1.*
import java.util.*
import kotlin.collections.HashMap


class Registrazione : Fragment(R.layout.registrazione) {

    val args: RegistrazioneArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val fDatabase: FirebaseFirestore = FirebaseFirestore.getInstance()
        val codiceNutrizionista = args.codiceNutrizionista;
        val flagNutrizionista = args.flagNutrizionista;
        val fAuth: FirebaseAuth = FirebaseAuth.getInstance()
        var userID: String

        val user= HashMap<String,Any>()

        val piano_al = HashMap<String,Any>()
        val diario_sett = HashMap<String,Any>()
        val ob_mensile = HashMap<String,Any>()
        val pesatura = HashMap<String,Any>()
        val paziente = ItemPaziente(piano_al, diario_sett, ob_mensile, pesatura)

        if(flagNutrizionista == true){
            Log.d("toolbar","cambia colore")
            registrazioneToolbar.setBackgroundColor(0xFF756ED5.toInt())
        }

        piano_al["isUpdated"] = true
        piano_al["lun"] = listOf("","","","")
        piano_al["mar"] = listOf("","","","")
        piano_al["mer"] = listOf("","","","")
        piano_al["gio"] = listOf("","","","")
        piano_al["ven"] = listOf("","","","")
        piano_al["sab"] = listOf("","","","")
        piano_al["dom"] = listOf("","","","")
        diario_sett["isUpdated"] = true
        diario_sett["data"] = listOf<Timestamp>()
        diario_sett["testo"] = listOf<String>()
        ob_mensile["isUpdated"] = true
        ob_mensile["peso"] = 0.0F
        ob_mensile["testo"] = ""
        pesatura["isUpdated"] = true
        pesatura["data"] = listOf<Timestamp>()
        pesatura["peso"] = listOf<Float>()

        registrazioneToolbar.setOnClickListener {
            findNavController().navigate(R.id.action_registrazione_to_registrazione1)
        }

        conferma_registrazione.setOnClickListener {
            val nome: String = reg_nome.text.toString().trim()
            val cognome: String = reg_cognome.text.toString().trim()
            val email: String = reg_mail.text.toString().trim()
            val password: String = reg_password.text.toString().trim()
            val passwordc: String = reg_passwordConfirm.text.toString().trim()
            val codice: String = codiceNutrizionista

            if (TextUtils.isEmpty(nome)) {
                reg_nome.error = "Il campo NOME è obbligatorio"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(cognome)) {
                reg_cognome.error = "Il campo COGNOME è obbligatorio"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(email)) {
                reg_mail.error = "Il campo MAIL è obbligatorio"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(password)) {
                reg_password.error = "Il campo PASSWORD è obbligatorio"
                return@setOnClickListener
            }
            if(TextUtils.isEmpty(passwordc)) {
                reg_passwordConfirm.error = "Il campo CONFERMA PASSWORD è obbligatorio"
                return@setOnClickListener
            }
            if(password.compareTo(passwordc) != 0) {
                reg_password.error = "Il campo PASSWORD e CONFERMA PASSWORD non coincidono"
                return@setOnClickListener
            }
            if (password.length < 6) {
                reg_password.error = "La password deve contenere almeno 6 caratteri"
                return@setOnClickListener
            }

            //REGISTRATION OF THE USER
            fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task->
                if (task.isSuccessful) {
                    userID= fAuth.currentUser.uid
                    user["Nome"] = nome
                    user["Cognome"] = cognome
                    user["Email"] = email
                    user["Password"] = password
                    user["Codice Nutrizionista"] = codice
                    user["isNutrizionista"]= flagNutrizionista
                    fDatabase.collection("users").document(userID).set(user).addOnCompleteListener {
                        if (it.isSuccessful) {
                            if(!flagNutrizionista){
                                fDatabase.collection("pazienti").document(userID).set(paziente).addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        Toast.makeText(context, "Registrazione riuscita", Toast.LENGTH_SHORT).show()
                                        findNavController().navigate(R.id.action_registrazione_to_paziente_homepage)
                                    } else{
                                        Toast.makeText(context, "Registrazione fallita", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }else{
                                fDatabase.collection("users").document(userID).update("Codice Nutrizionista", fAuth.currentUser.uid)
                                Toast.makeText(context, "Registrazione riuscita", Toast.LENGTH_SHORT).show()
                                findNavController().navigate(R.id.action_registrazione_to_nutrizionista_homepage)
                            }

                        } else {
                            Toast.makeText(context, "Registrazione fallita", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Registrazione fallita, email già esistente", Toast.LENGTH_SHORT).show()
                }
            }

        }

    }

}






