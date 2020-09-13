package com.legendbois.memeindexer.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.legendbois.memeindexer.R
import com.legendbois.memeindexer.database.UsageHistory

// TODO: New Base RV class to be inherited by all these, will reduce code repetition

class SearchHistoryRV internal constructor(
    context: Context,
    private val listener: (UsageHistory) -> Unit
): RecyclerView.Adapter<SearchHistoryRV.SearchHistoryRViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var actions = emptyList<UsageHistory>()

    inner class SearchHistoryRViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val text: TextView = itemView.findViewById(R.id.searchhistory_text)

        fun bind(action: UsageHistory){
            text.text = action.pathOrQuery
            itemView.setOnClickListener { listener(action) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHistoryRViewHolder {
        val itemView = inflater.inflate(R.layout.searchhistory_adapter_item, parent, false)
        return SearchHistoryRViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SearchHistoryRViewHolder, position: Int) {
        val current = actions[position]
        holder.bind(current)
    }

    internal fun setActions(actions: List<UsageHistory>){
        this.actions=actions
        notifyDataSetChanged()
    }

    override fun getItemCount() = actions.size
}
