package com.legendbois.memeindexer.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.legendbois.memeindexer.R
import com.legendbois.memeindexer.database.MemeFileDao
import com.legendbois.memeindexer.database.MemeFilesDatabase
import kotlinx.coroutines.launch

class SearchMemesFragment: Fragment(), SearchView.OnQueryTextListener {
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
        search.isIconifiedByDefault = false
        search.clearFocus()
        search.setOnQueryTextListener(this)
        return root
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        val db = MemeFilesDatabase.getDatabase(context!!).memeFileDao
        lifecycleScope.launch {
            searchText(query, db)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }

    suspend fun searchText(text: String?, db: MemeFileDao){
        if (text != null) {
            val results = db.loadTopByText("%$text%")
            for (result in results){
                Log.v(TAG, "${result.rowid}, ${result.filename}, ${result.fileuri}")
            }
        }

    }
}