package it.polito.ic2020.did2020.fiitapp

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.paziente_foto_profilo.*
import java.io.File


class Paziente_foto_profilo: Fragment(R.layout.paziente_foto_profilo) {

   // val profileImageView: ImageView= fotoPaziente
   // val saveButton: Button= save
    lateinit var filepath: Uri
    val fDatabase = FirebaseFirestore.getInstance()
    val fAuth = FirebaseAuth.getInstance()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        fotoPaziente.setOnClickListener{
            startFileChooser()
        }

        toAppBar.setNavigationOnClickListener {
         findNavController().navigate(R.id.action_paziente_foto_profilo_to_paziente_homepage)
        }

        save.setOnClickListener {
            uploadFile()
        }

        val imageName = fAuth.currentUser.uid
        val fStorage = FirebaseStorage.getInstance().reference.child("image/$imageName.jpg")
        val localfile = File.createTempFile("tempImage","jpg")

        fStorage.getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            fotoPaziente.setImageBitmap(bitmap)
        }


    }

    private fun uploadFile() {
        if(filepath!=null){
            var pd= ProgressDialog(context)
            pd.setTitle("Caricamento")
            pd.show()

            var imageRef = FirebaseStorage.getInstance().reference.child("image/".plus(fAuth.currentUser.uid).plus(".jpg"))
            imageRef.putFile(filepath).addOnSuccessListener { p0->
                pd.dismiss()
                Toast.makeText(context,"Immagine Caricata", Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.action_paziente_foto_profilo_to_paziente_homepage)
            }.addOnFailureListener{ p0->
                pd.dismiss()
                Toast.makeText(context,"Impossibile Caricare l'immagine", Toast.LENGTH_LONG).show()
            }.addOnProgressListener { p0->
                var progress = (100.0 * p0.bytesTransferred) / p0.totalByteCount
                pd.setMessage("Caricamento al ${progress.toInt()}%")
            }

        }
    }

    private fun startFileChooser() {
        var i = Intent()
        i.setType("image/*")
        i.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(i, "Choose Picture"), 111)
    }
    override fun  onActivityResult(requestCode: Int,resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode,resultCode,data)
        if(requestCode==111 && resultCode == Activity.RESULT_OK && data!=null) {
            filepath = data.data!!
            var bitmap= MediaStore.Images.Media.getBitmap(activity?.contentResolver,filepath)
            fotoPaziente.setImageBitmap(bitmap)
            save.isEnabled = true
        }

    }
}







