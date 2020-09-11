package com.legendbois.memeindexer.ui.main

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SearchView
import androidx.lifecycle.Observer
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.legendbois.memeindexer.R
import com.legendbois.memeindexer.database.MemeFile
import com.legendbois.memeindexer.database.UsageHistory
import com.legendbois.memeindexer.dialogs.MemeInfoDialogFragment
import com.legendbois.memeindexer.viewmodel.MemeFileViewModel
import com.legendbois.memeindexer.viewmodel.MemesHelper
import com.legendbois.memeindexer.viewmodel.UsageHistoryViewModel
import kotlinx.coroutines.launch
import java.util.*

class HistoryFragment: Fragment(){
    private lateinit var usageHistoryViewModel: UsageHistoryViewModel
    private lateinit var adapter: HistoryRVAdapter
    companion object{
        const val TAG = "HistoryFragment"

        @JvmStatic
        fun newInstance(): HistoryFragment{
            return HistoryFragment()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.history_frag, container, false)
        val application = requireNotNull(this.activity).application
        val recyclerView = root.findViewById<RecyclerView>(R.id.history_recyclerview)

        //Thanks to https://antonioleiva.com/recyclerview-listener/
        adapter = HistoryRVAdapter(application.applicationContext){ item, share ->
            when(share){
                0 -> imagePopup(item.pathOrQuery)
                else -> null
            }
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        usageHistoryViewModel = ViewModelProvider(this).get(UsageHistoryViewModel::class.java)
        usageHistoryViewModel.getSharedMemes().observe(viewLifecycleOwner, Observer {actions ->
            adapter.setActions(actions)
        })
        return root
    }

    fun shareImage(filepath: String){
        if (context != null){
            MemesHelper.shareImage(context!!.applicationContext, filepath)
        }
    }

    fun imagePopup(filepath: String){
        if(context != null){
            MemesHelper.imagePopup(context!!, filepath)
        }
    }
}