package com.legendbois.memeindexer.ui.main

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.legendbois.memeindexer.R
import com.legendbois.memeindexer.database.MemeFile
import kotlinx.android.synthetic.main.memefile_adapter_item.view.*

class SearchRVAdapter internal constructor(
    context: Context
): RecyclerView.Adapter<SearchRVAdapter.SearchRViewHolder>(){

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var memes = emptyList<MemeFile>()

    inner class SearchRViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val filename: TextView = itemView.findViewById(R.id.item_title)
        val summary: TextView = itemView.findViewById(R.id.item_detail)
        val image: ImageView = itemView.findViewById(R.id.item_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchRViewHolder {
        val itemView = inflater.inflate(R.layout.memefile_adapter_item, parent, false)
        return SearchRViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SearchRViewHolder, position: Int) {
        val current = memes[position]
        holder.filename.text = current.filename
        holder.summary.text = current.ocrtext!!.substring(0, 200) +"..."
        holder.image.setImageURI(Uri.parse(current.fileuri))

    }

    internal fun setMemes(memes: List<MemeFile>){
        this.memes=memes
        notifyDataSetChanged()
    }

    override fun getItemCount() = memes.size

}