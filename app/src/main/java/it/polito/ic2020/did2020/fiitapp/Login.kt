package it.polito.ic2020.did2020.fiitapp

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.login.*


class Login : Fragment(R.layout.login) {

    val args: LoginArgs by navArgs()
    var email = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            email = savedInstanceState.getString("NOME_NUTR", "")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity?.let { (activity as ActivityMain?)?.updateStatusBarColor(it.getColor(R.color.purple_dark)) }



        registration_view.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_registrazione1)
        }

        login_main.setOnClickListener {
            onClick()
        }

        if(args.mailInserita != " "){
            Log.e("ENTRO NELL IF", args.mailInserita)
            login_mail.setText(args.mailInserita)
        }


    }

    private fun onClick() {
        val fDatabase = FirebaseFirestore.getInstance()
        val fAuth = FirebaseAuth.getInstance()
        email = login_mail.text.toString().trim()
        val password: String = login_password.text.toString().trim()

        if (TextUtils.isEmpty(email)) {
            login_mail.error = "Il campo MAIL è obbligatorio"
            return
        }
        if (TextUtils.isEmpty(password)) {
            login_password.error = "Il campo PASSWORD è obbligatorio"
            return
        }
        if (password.length < 6) {
            login_password.error = "La password deve contenere almeno 6 caratteri"
            return
        }

        // authenticate the user
        fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
               Toast.makeText(context, "Login Riuscito", Toast.LENGTH_SHORT).show()
                fDatabase.collection("users").document(fAuth.currentUser.uid).get().addOnCompleteListener {
                    if(it.isSuccessful){
                        if(it.result!!.data!!["isNutrizionista"] == true){
                            findNavController().navigate(R.id.action_login_to_nutrizionista_homepage)
                        }else{
                            findNavController().navigate(R.id.action_login_to_paziente_homepage)
                        }
                    }
                }
            } else{
                Toast.makeText(context, "Login Fallito", Toast.LENGTH_SHORT).show()
                Log.d("LOGIN", "fallito")
                val direction = LoginDirections.actionLoginSelf(login_mail.text.toString())
                findNavController().navigate(direction)

            }

        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("EMAIL", email)
    }
}