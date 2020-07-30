package com.legendbois.memeindexer.ui.main

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.legendbois.memeindexer.R
import com.legendbois.memeindexer.database.MemeFile

class SearchRVAdapter internal constructor(
    context: Context,
    private val listener: (MemeFile, Boolean) -> Unit
): RecyclerView.Adapter<SearchRVAdapter.SearchRViewHolder>(){

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var memes = emptyList<MemeFile>()

    inner class SearchRViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val image: ImageView = itemView.findViewById(R.id.recycler_item_image)
        // TODO: Make the share button work
        val button: ImageButton = itemView.findViewById(R.id.recycler_item_button)

        fun bind(memefile: MemeFile){
            // TODO: Scaled down version and not on main thread, try Picasso?
            image.setImageBitmap(BitmapFactory.decodeFile(memefile.filepath))
            image.setOnClickListener { listener(memefile, false) }
            button.setOnClickListener { listener(memefile, true) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchRViewHolder {
        val itemView = inflater.inflate(R.layout.memefile_adapter_item, parent, false)
        return SearchRViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SearchRViewHolder, position: Int) {
        val current = memes[position]
        holder.bind(current)

    }

    internal fun setMemes(memes: List<MemeFile>){
        this.memes=memes
        notifyDataSetChanged()
    }

    override fun getItemCount() = memes.size



}