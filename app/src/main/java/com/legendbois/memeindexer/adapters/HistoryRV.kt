package com.legendbois.memeindexer.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.legendbois.memeindexer.R
import com.legendbois.memeindexer.database.UsageHistory
import com.squareup.picasso.Picasso
import java.io.File

class HistoryRV internal constructor(
    context: Context,
    private val listener: (UsageHistory, Int) -> Unit
): RecyclerView.Adapter<HistoryRV.HistoryRViewHolder>(){

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var actions = emptyList<UsageHistory>()

    inner class HistoryRViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val image: ImageView = itemView.findViewById(R.id.recycler_history_image)

        fun bind(actions: UsageHistory){
            // TODO: Add placeholder and error image, preferably open source or will have to create
            // DAYUM picasso made the searching smooth af
            Picasso.get().load(File(actions.pathOrQuery)).into(image)
            //image.setImageBitmap(BitmapFactory.decodeFile())
            image.setOnClickListener { listener(actions, 0) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryRViewHolder {
        val itemView = inflater.inflate(R.layout.history_adapter_item, parent, false)
        return HistoryRViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HistoryRViewHolder, position: Int) {
        val current = actions[position]
        holder.bind(current)

    }

    internal fun setActions(actions: List<UsageHistory>){
        this.actions=actions
        notifyDataSetChanged()
    }

    override fun getItemCount() = actions.size



}