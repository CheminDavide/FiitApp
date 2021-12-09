package it.polito.ic2020.did2020.fiitapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import it.polito.ic2020.did2020.fiitapp.Model.RSSObject

class AdapterFeed(private val rssObject: RSSObject, private val mContext: Context)
    :  RecyclerView.Adapter<AdapterFeed.ViewHolder>() {

    private val inflater:LayoutInflater
    init{
        inflater = LayoutInflater.from(mContext)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = inflater.inflate(R.layout.item_feed, parent,false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtTitle.text = rssObject.items[position].title
        holder.txtContent.text = rssObject.items[position].content
        holder.txtPubdate.text = rssObject.items[position].pubDate

        holder.itemView.setOnClickListener {
            val browserIntent =Intent(Intent.ACTION_VIEW, Uri.parse(rssObject.items[position].link))
            mContext.startActivity(browserIntent)
        }

    }

    override fun getItemCount(): Int {
        return rssObject.items.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtTitle:TextView = itemView.findViewById(R.id.txtTitle)
        var txtPubdate:TextView = itemView.findViewById(R.id.txtPubdate)
        var txtContent:TextView = itemView.findViewById(R.id.txtContent)
    }
}