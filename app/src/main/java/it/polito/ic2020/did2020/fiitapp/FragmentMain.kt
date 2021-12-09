package it.polito.ic2020.did2020.fiitapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.frgmt_main.*


class FragmentMain : Fragment(R.layout.frgmt_main) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity?.let { (activity as ActivityMain?)?.updateStatusBarColor(it.getColor(R.color.purple_dark)) }

        val mAuth = FirebaseAuth.getInstance()
        val mFirestoreDB = FirebaseFirestore.getInstance()



        if(mAuth.currentUser != null){
            mFirestoreDB.collection("users").document(mAuth.currentUser.uid).get().addOnCompleteListener {
                if(it.isSuccessful){
                    if(it.result!!.data!!["isNutrizionista"].toString() == "false"){
                        findNavController().navigate(R.id.action_frgmt_main_to_paziente_homepage)
                    }else{
                        findNavController().navigate(R.id.action_frgmt_main_to_nutrizionista_homepage)
                    }
                }
            }
        }



        login_main.setOnClickListener {

            findNavController().navigate(R.id.action_main_to_login)
        }
        signup_button.setOnClickListener{
            findNavController().navigate(R.id.action_frgmt_main_to_registrazione1)
        }



    }



}