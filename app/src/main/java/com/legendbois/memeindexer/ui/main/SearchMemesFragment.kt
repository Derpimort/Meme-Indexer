package com.legendbois.memeindexer.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.legendbois.memeindexer.R
import com.legendbois.memeindexer.viewmodel.MemeFileViewModel


class SearchMemesFragment: Fragment(), SearchView.OnQueryTextListener {
    private lateinit var memeFileViewModel: MemeFileViewModel
    private lateinit var adapter: SearchRVAdapter
    companion object{
        const val TAG = "SearchMemesFragment"
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
        val application = requireNotNull(this.activity).application
        val recyclerView = root.findViewById<RecyclerView>(R.id.searchmemes_recyclerview)
        adapter = SearchRVAdapter(application.applicationContext)
        val mDividerItemDecoration = DividerItemDecoration(
            recyclerView.getContext(),
            DividerItemDecoration.VERTICAL
        )
        recyclerView.addItemDecoration(mDividerItemDecoration)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(application)
        memeFileViewModel = ViewModelProvider(this).get(MemeFileViewModel::class.java)
        search.isIconifiedByDefault = false
        search.clearFocus()
        search.setOnQueryTextListener(this)
        return root
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            memeFileViewModel.searchMemes("%$query%").observe(this, Observer { memes ->
                adapter.setMemes(memes)
            })
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }
}