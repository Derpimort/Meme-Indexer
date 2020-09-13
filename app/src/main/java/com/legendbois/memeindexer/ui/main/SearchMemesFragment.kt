package com.legendbois.memeindexer.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.legendbois.memeindexer.R
import com.legendbois.memeindexer.adapters.SearchHistoryRV
import com.legendbois.memeindexer.adapters.SearchRV
import com.legendbois.memeindexer.database.MemeFile
import com.legendbois.memeindexer.database.UsageHistory
import com.legendbois.memeindexer.dialogs.MemeInfoDialogFragment
import com.legendbois.memeindexer.viewmodel.MemeFileViewModel
import com.legendbois.memeindexer.MemesHelper
import com.legendbois.memeindexer.viewmodel.UsageHistoryViewModel
import kotlinx.coroutines.launch
import java.util.*


class SearchMemesFragment: Fragment(), SearchView.OnQueryTextListener {
    private lateinit var memeFileViewModel: MemeFileViewModel
    private lateinit var usageHistoryViewModel: UsageHistoryViewModel
    private lateinit var adapter: SearchRV
    companion object{
        const val TAG = "SearchMemesFragment"

        @JvmStatic
        fun newInstance(): SearchMemesFragment{
            return SearchMemesFragment()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.searchmemes_frag, container, false)
        val search: SearchView = root.findViewById(R.id.searchmemes_search)

        setupSearchRV(root)
        memeFileViewModel = ViewModelProvider(this).get(MemeFileViewModel::class.java)
        usageHistoryViewModel = ViewModelProvider(this).get(UsageHistoryViewModel::class.java)
        setupSearchHistoryRV(root)
        search.isFocusable=false
        search.isIconifiedByDefault = false
        search.clearFocus()
        search.setOnQueryTextListener(this)
        return root
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            view!!.findViewById<SearchView>(R.id.searchmemes_search).clearFocus()
            memeFileViewModel.searchMemes("%${query.toLowerCase(Locale.ROOT)}%").observe(viewLifecycleOwner, Observer { memes ->
                adapter.setMemes(memes)
            })
            addUsageHistory(query, 1, 1)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }

    fun setupSearchRV(root: View){
        val application = requireNotNull(this.activity).application
        val recyclerView = root.findViewById<RecyclerView>(R.id.searchmemes_recyclerview)

        //Thanks to https://antonioleiva.com/recyclerview-listener/
        adapter =
            SearchRV(application.applicationContext) { item, share ->
                when (share) {
                    0 -> imagePopup(item.filepath)
                    1 -> shareImage(item.filepath)
                    else -> infoPopup(item)
                }
            }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(context, 2)
    }

    fun setupSearchHistoryRV(root: View){
        val application = requireNotNull(this.activity).application
        val recyclerView = root.findViewById<RecyclerView>(R.id.searchmemes_historyrv)

        //Thanks to https://antonioleiva.com/recyclerview-listener/
        val shAdapter =
            SearchHistoryRV(application.applicationContext) { item ->
                root.findViewById<SearchView>(R.id.searchmemes_search).setQuery(item.pathOrQuery, true)
                // onQueryTextSubmit(item.pathOrQuery)
            }
        recyclerView.adapter = shAdapter
        recyclerView.layoutManager = LinearLayoutManager(application, LinearLayoutManager.HORIZONTAL, false)
        usageHistoryViewModel.getSearchedTerms().observe(viewLifecycleOwner, Observer { actions ->
            shAdapter.setActions(actions)
        })
    }

    fun addUsageHistory(queryOrPath: String, actionId: Int, extraInfo: Int?){
        lifecycleScope.launch {
            usageHistoryViewModel.insert(
                UsageHistory(
                    pathOrQuery = queryOrPath,
                    actionId = actionId,
                    extraInfo = extraInfo
                )
            )
        }
    }

    fun shareImage(filepath: String){
        if (context != null){
            MemesHelper.shareImage(context!!.applicationContext, filepath)
            addUsageHistory(filepath, 2, 1)
        }

    }

    fun imagePopup(filepath: String){
        if(context != null){
            MemesHelper.imagePopup(context!!, filepath)
        }

    }

    fun infoPopup(memefile: MemeFile){
        val dialog = MemeInfoDialogFragment.newInstance(memefile)
        dialog.show(parentFragmentManager, "meme_info")
    }
}