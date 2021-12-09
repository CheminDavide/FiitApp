package it.polito.ic2020.did2020.fiitapp

import android.graphics.BitmapFactory
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class AdapterNutrizionistaHomepage(private val pazienti: MutableList<ItemUser>, val vediDettaglio: (String) -> Unit)
    : RecyclerView.Adapter<AdapterNutrizionistaHomepage.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_nutrizionista_homepage, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = pazienti[position]
        holder.textItem.text = item.cognome.plus(" ").plus(item.nome)
        holder.itemView.setOnClickListener {
            vediDettaglio(item.id)
        }
        //Glide.with(holder.itemView).load(item.image).into(holder.imageUser)
        val fAuth = FirebaseAuth.getInstance()
        val imageName = fAuth.currentUser.uid
        val localfile = File.createTempFile("tempImage","jpg")

        val fStorage = FirebaseStorage.getInstance().reference.child("image/${item.id}.jpg")

        fStorage.getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            holder.imageUser.setImageBitmap(bitmap)
        }

    }

    override fun getItemCount(): Int = pazienti.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textItem: TextView = view.findViewById(R.id.nomePaziente)
        val imageUser: ImageView = view.findViewById(R.id.fotoUser)
    }
}