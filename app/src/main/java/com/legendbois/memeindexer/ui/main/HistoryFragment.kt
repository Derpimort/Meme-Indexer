package com.legendbois.memeindexer.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.legendbois.memeindexer.R
import com.legendbois.memeindexer.adapters.HistoryRV
import com.legendbois.memeindexer.MemesHelper
import com.legendbois.memeindexer.viewmodel.UsageHistoryViewModel

class HistoryFragment: Fragment(){
    private lateinit var usageHistoryViewModel: UsageHistoryViewModel
    private lateinit var adapter: HistoryRV
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
        adapter =
            HistoryRV(application.applicationContext) { item, share ->
                when (share) {
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