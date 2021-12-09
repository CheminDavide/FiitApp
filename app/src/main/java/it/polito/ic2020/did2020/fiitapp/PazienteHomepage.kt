package it.polito.ic2020.did2020.fiitapp

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.paziente_homepage.*
import java.io.ByteArrayOutputStream
import java.io.File


class PazienteHomepage : Fragment(R.layout.paziente_homepage) {

    val fDatabase = FirebaseFirestore.getInstance()
    val fAuth = FirebaseAuth.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity?.let { (activity as ActivityMain?)?.updateStatusBarColor(it.getColor(R.color.green_dark)) }

        loadProfile()

        rilevaPeso_btn.setOnClickListener {
            findNavController().navigate(R.id.action_paziente_homepage_to_paziente_pesatura)
        }
        btn_pianoAlimentareP.setOnClickListener {
            findNavController().navigate(R.id.action_paziente_homepage_to_paziente_pianoalimentare)
        }
        btn_diarioSettimanaleP.setOnClickListener {
            findNavController().navigate(R.id.action_paziente_homepage_to_paziente_diariosettimanale)
        }
        btn_obiettivoMensileP.setOnClickListener {
            findNavController().navigate(R.id.action_paziente_homepage_to_paziente_obiettivomensile)
        }
        btn_approfondimentiP.setOnClickListener {
            findNavController().navigate(R.id.action_paziente_homepage_to_paziente_approfondimento)
        }
        btnImpostazioni.setOnClickListener{
            findNavController().navigate(R.id.action_paziente_homepage_to_pazienteImpostazioni)
        }

        logout.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            findNavController().navigate(R.id.action_paziente_homepage_to_frgmt_main)
        }
        cardvModificaFoto.setOnClickListener{
            updateProfilePicture()
        }

    }

    private fun loadProfile(){
        val dbUsers = fDatabase.collection("users").document(fAuth.currentUser.uid)
        val dbPaziente = fDatabase.collection("pazienti").document(fAuth.currentUser.uid)


        val imageName = fAuth.currentUser.uid
        val fStorage = FirebaseStorage.getInstance().reference.child("image/$imageName.jpg")
        val localfile = File.createTempFile("tempImage", "jpg")

        fStorage.getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            profilePicPaziente.setImageBitmap(bitmap)
        }

        dbUsers.get().addOnCompleteListener { docUsers ->
            dbPaziente.get().addOnCompleteListener { docPazienti ->
                nomeUtente.text = docUsers.result?.get("Nome").toString().plus(" ").plus(
                    docUsers.result?.get(
                        "Cognome"
                    ).toString()
                )


                val pesi:List<Float> = docPazienti.result?.get("pesatura.peso") as List<Float>
                if (pesi.isNotEmpty() ) {
                    lastPeso.text = pesi[0].toString().plus("kg")
                }
                if(!(docPazienti.result?.get("piano_al.isUpdated") as Boolean)) {
                    notifyPiano.visibility = View.VISIBLE
                }
                if(!(docPazienti.result?.get("ob_mensile.isUpdated") as Boolean)) {
                    notifyObiettivo.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun updateProfilePicture() {
        val i = Intent()
        i.type = "image/*"
        i.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(i, "Modifica immagine profilo"), 111)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==111 && resultCode == Activity.RESULT_OK && data!=null) {
            val filepath = data.data

            if(filepath!=null){
                val pd= ProgressDialog(context)
                pd.setTitle("Caricamento")
                pd.show()

                val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, filepath)
                profilePicPaziente.setImageBitmap(bitmap)

                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos)
                val imageData: ByteArray = baos.toByteArray()

                val imageRef = FirebaseStorage.getInstance().reference.child(
                    "image/".plus(fAuth.currentUser.uid).plus(
                        ".jpg"
                    )
                )
                imageRef.putBytes(imageData).addOnSuccessListener {
                    pd.dismiss()
                    Toast.makeText(context, "Immagine Caricata", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener{
                    pd.dismiss()
                    Toast.makeText(context, "Impossibile Caricare l'immagine", Toast.LENGTH_SHORT).show()
                }.addOnProgressListener { p0->
                    val progress = (100.0 * p0.bytesTransferred) / p0.totalByteCount
                    pd.setMessage("Caricamento al ${progress.toInt()}%")
                }
            }
        }

    }

}

