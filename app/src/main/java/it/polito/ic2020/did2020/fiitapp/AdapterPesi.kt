package it.polito.ic2020.did2020.fiitapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class AdapterPesi (private val pesi: MutableList<ItemPeso>) : RecyclerView.Adapter<AdapterPesi.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_peso, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = pesi[position]
        holder.pesoTextItem.text= item.peso.toString().plus(" kg")
        holder.dataTextItem.text= getDateTime(item.data)
    }

    override fun getItemCount(): Int = pesi.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val pesoTextItem: TextView = view.findViewById(R.id.peso_txt)
        val dataTextItem: TextView = view.findViewById(R.id.data_txt)
    }

    private fun getDateTime(s: String): String? {
        return try {
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN)
            val date = Date(s.toLong())
            formatter.format(date)
        } catch (e: Exception) {
            "error"
        }
    }
}