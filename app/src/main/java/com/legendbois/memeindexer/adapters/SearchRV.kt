package com.legendbois.memeindexer.adapters

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.legendbois.memeindexer.R
import com.legendbois.memeindexer.database.MemeFile
import com.squareup.picasso.Picasso
import java.io.File

class SearchRV internal constructor(
    context: Context,
    private val listener: (MemeFile, Int) -> Unit
): RecyclerView.Adapter<SearchRV.SearchRViewHolder>(){

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var memes = emptyList<MemeFile>()

    inner class SearchRViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val image: ImageView = itemView.findViewById(R.id.recycler_item_image)
        val button: ImageButton = itemView.findViewById(R.id.recycler_item_button)
        val infoButton: ImageButton = itemView.findViewById(R.id.recycler_item_info)
        val selectedImage: ImageView = itemView.findViewById(R.id.recycler_item_image_selected)

        fun bind(memefile: MemeFile, position: Int){
            // TODO: Add placeholder and error image, preferably open source or will have to create
            // DAYUM picasso made the searching smooth af
            Picasso.get().load(File(memefile.filepath)).fit().centerCrop(Gravity.TOP).into(image)
            //image.setImageBitmap(BitmapFactory.decodeFile())
            image.setOnClickListener {
                listener(memefile, 0)
                notifyItemChanged(position)}
            selectedImage.setOnClickListener {
                listener(memefile, 0)
                notifyItemChanged(position)
            }
            button.setOnClickListener { listener(memefile, 1) }
            infoButton.setOnClickListener { listener(memefile, 2) }
            selectedImage.visibility = when(memefile.memeIsSelected){
                true -> View.VISIBLE
                false -> View.GONE
            }
            memefile.position = position

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchRViewHolder {
        val itemView = inflater.inflate(R.layout.memefile_adapter_item, parent, false)
        return SearchRViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SearchRViewHolder, position: Int) {
        val current = memes[position]
        holder.bind(current, position)

    }

    internal fun setMemes(memes: List<MemeFile>){
        this.memes=memes
        notifyDataSetChanged()
    }

    override fun getItemCount() = memes.size



}