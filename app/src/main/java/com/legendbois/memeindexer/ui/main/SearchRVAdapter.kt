package com.legendbois.memeindexer.ui.main

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.legendbois.memeindexer.R
import com.legendbois.memeindexer.database.MemeFile

class SearchRVAdapter internal constructor(
    private val context: Context,
    private val listener: (MemeFile) -> Unit
): RecyclerView.Adapter<SearchRVAdapter.SearchRViewHolder>(){

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var memes = emptyList<MemeFile>()

    inner class SearchRViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val filename: TextView = itemView.findViewById(R.id.item_title)
        val summary: TextView = itemView.findViewById(R.id.item_detail)
        val image: ImageView = itemView.findViewById(R.id.item_image)

        fun bind(memefile: MemeFile){
            filename.text = memefile.filename
            summary.text = memefile.ocrtext

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                image.setImageBitmap(context.contentResolver.loadThumbnail(
                    Uri.parse(memefile.fileuri),
                    Size(100, 100),
                    null
                )
                )
            }
            else{
                image.setImageURI(Uri.parse(memefile.fileuri))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchRViewHolder {
        val itemView = inflater.inflate(R.layout.memefile_adapter_item, parent, false)
        return SearchRViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SearchRViewHolder, position: Int) {
        val current = memes[position]
        holder.bind(current)
        holder.itemView.setOnClickListener { listener(current) }
    }

    internal fun setMemes(memes: List<MemeFile>){
        this.memes=memes
        notifyDataSetChanged()
    }

    override fun getItemCount() = memes.size



}