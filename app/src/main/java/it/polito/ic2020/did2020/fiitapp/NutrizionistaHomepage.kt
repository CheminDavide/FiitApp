package it.polito.ic2020.did2020.fiitapp

import android.app.Activity
import android.app.ProgressDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.nutrizionista_homepage.*
import java.io.ByteArrayOutputStream
import java.io.File


class NutrizionistaHomepage : Fragment(R.layout.nutrizionista_homepage){

    private val fDatabase = FirebaseFirestore.getInstance()
    private val fAuth = FirebaseAuth.getInstance()
    private val fStorage = FirebaseStorage.getInstance().reference
    private var nome_nutr = ""

    private val viewModel: CurrentPaziente by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity?.let { (activity as ActivityMain?)?.updateStatusBarColor(it.getColor(R.color.purple_dark)) }

        topAppBar.inflateMenu(R.menu.homepage_nustrizionista_appbar)

        topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.logout -> {
                    FirebaseAuth.getInstance().signOut()
                    findNavController().navigate(R.id.action_nutrizionista_homepage_to_frgmt_main)
                    true
                }

                R.id.settings -> {
                    findNavController().navigate(R.id.action_nutrizionista_homepage_to_nutrizionista_impostazioni)
                    true
                }

                else -> {
                    super.onOptionsItemSelected(it)
                }
            }
        }

        val _dbData = mutableListOf<ItemUser>()

        loadProfile()

        fDatabase.collection("users").get().addOnSuccessListener { documents ->
            for (document in documents) {
                if((document.get("isNutrizionista") == false) && (document.get("Codice Nutrizionista").toString() == fAuth.currentUser.uid)){
                    _dbData.add(
                        ItemUser(
                            document.id , document.get("Nome").toString(), document.get("Cognome").toString()
                        )
                    )
                }
            }

            _dbData.sortBy { i -> i.cognome }
            Log.e("lista db", _dbData.toString())
            listaPazienti.layoutManager = LinearLayoutManager(context)
            listaPazienti.isNestedScrollingEnabled = false
            listaPazienti.adapter = AdapterNutrizionistaHomepage(_dbData) {
                viewModel.currentId(it)
                findNavController().navigate(R.id.action_nutrizionista_homepage_to_nutrizionista_dettaglio_paziente)
            }

        }

        code_button.setOnClickListener{
            val clipboard: ClipboardManager = activity?.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip: ClipData = ClipData.newPlainText("User ID", fAuth.currentUser.uid);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(context, "Codice copiato", Toast.LENGTH_LONG).show()
        }

        cardModificaFoto.setOnClickListener{
            updateProfilePicture()
        }

    }


    private fun loadProfile() {
        val imageName = fAuth.currentUser.uid
        val localfile = File.createTempFile("tempImage","jpg")

        fStorage.child("image/$imageName.jpg").getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            fotoDettaglioPaziente.setImageBitmap(bitmap)
        }

        val dbUsers = fDatabase.collection("users").document(fAuth.currentUser.uid)
        dbUsers.get().addOnCompleteListener { docUsers ->
            nome_nutr = docUsers.result?.get("Nome").toString().plus(" ").plus(docUsers.result?.get("Cognome").toString())
            nomeUtente.text = nome_nutr
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
        if (requestCode == 111 && resultCode == Activity.RESULT_OK && data != null) {
            val filepath = data.data

            if (filepath != null) {
                val pd = ProgressDialog(context)
                pd.setTitle("Caricamento")
                pd.show()

                val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, filepath)
                fotoDettaglioPaziente.setImageBitmap(bitmap)

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
                    Toast.makeText(context, "Immagine Caricata", Toast.LENGTH_LONG).show()
                }.addOnFailureListener {
                    pd.dismiss()
                    Toast.makeText(context, "Impossibile Caricare l'immagine", Toast.LENGTH_LONG)
                        .show()
                }.addOnProgressListener { p0 ->
                    val progress = (100.0 * p0.bytesTransferred) / p0.totalByteCount
                    pd.setMessage("Caricamento al ${progress.toInt()}%")
                }
            }
        }
    }

}

