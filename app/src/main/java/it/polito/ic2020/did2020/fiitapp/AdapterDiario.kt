package it.polito.ic2020.did2020.fiitapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class AdapterDiario(private val values: MutableList<ItemDiario>)
    : RecyclerView.Adapter<AdapterDiario.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_nutrizionista_diario,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.textItem.text = item.testoDiario
        holder.dataItem.text = getDateTime(item.dataDiario)
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textItem: TextView = view.findViewById(R.id.item_testo)
        val dataItem: TextView = view.findViewById(R.id.item_data)

        override fun toString(): String {
            return super.toString() + " '" + textItem.text + "'"
        }
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