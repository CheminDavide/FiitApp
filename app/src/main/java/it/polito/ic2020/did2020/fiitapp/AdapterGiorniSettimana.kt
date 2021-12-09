package it.polito.ic2020.did2020.fiitapp


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView


class AdapterGiorniSettimana(private val values: List<ItemGiorno>, private val isNutrizionista: Boolean)
    : RecyclerView.Adapter<AdapterGiorniSettimana.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_giorno, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.contentView.text = item.giorno
        holder.itemView.setOnClickListener {
            vediDettaglio(it, item.item_id)
        }
    }

    override fun getItemCount(): Int = 7

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val contentView: TextView = view.findViewById(R.id.item_giorno)

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }

    private fun vediDettaglio(view: View, id: Int) {
        val bundle = bundleOf("index" to id)
        if (isNutrizionista) {
            Navigation.findNavController(view).navigate(R.id.action_nutrizionista_pianoalimentare_to_nutrizionista_pianodettaglio, bundle)
        }
        else {
            Navigation.findNavController(view).navigate(R.id.action_paziente_pianoalimentare_to_paziente_pianodettaglio, bundle)
        }
    }
}