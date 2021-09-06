package com.legendbois.memeindexer.ui.main

import android.content.Context
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
import com.legendbois.memeindexer.ConstantsHelper.USAGE_HISTORY_ACTIONS
import com.legendbois.memeindexer.R
import com.legendbois.memeindexer.adapters.SearchHistoryRV
import com.legendbois.memeindexer.adapters.SearchRV
import com.legendbois.memeindexer.database.MemeFile
import com.legendbois.memeindexer.database.UsageHistory
import com.legendbois.memeindexer.viewmodel.MemeFileViewModel
import com.legendbois.memeindexer.viewmodel.UsageHistoryViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.searchmemes_frag.*
import kotlinx.coroutines.launch
import java.util.*


class SearchMemesFragment: Fragment(), SearchView.OnQueryTextListener {
    private lateinit var memeFileViewModel: MemeFileViewModel
    private lateinit var usageHistoryViewModel: UsageHistoryViewModel
    private lateinit var adapter: SearchRV
    private var positiveScrolled: Boolean = false
    private lateinit var memeCallback: OnMemeClickedListener
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


        memeFileViewModel = ViewModelProvider(this).get(MemeFileViewModel::class.java)
        usageHistoryViewModel = ViewModelProvider(this).get(UsageHistoryViewModel::class.java)
        setupSearchRV(root)
        setupSearchHistoryRV(root)
        search.isFocusable=false
        search.isIconifiedByDefault = false
        search.clearFocus()
        search.setOnQueryTextListener(this)
        return root
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            requireView().findViewById<SearchView>(R.id.searchmemes_search).clearFocus()
            memeFileViewModel.searchMemes("%${query.toLowerCase(Locale.ROOT)}%").observe(
                viewLifecycleOwner,
                Observer { memes ->
                    adapter.setMemes(memes)
                })
            addUsageHistory(query, USAGE_HISTORY_ACTIONS.getOrDefault("search", 1), 1)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }

    interface OnMemeClickedListener {
        fun onMemeShared(filepath: String)
        fun onMemeClicked(filepath: String)
        fun onMemeInfoClicked(memefile: MemeFile)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try{
            memeCallback = activity as OnMemeClickedListener
        }
        catch (e: ClassCastException){
            throw ClassCastException(activity.toString() + " must implement OnMemeClickedListener")
        }
    }

    fun toggleScrolled(boolean: Boolean){
        if(adapter.itemCount !=0) {
            if (boolean) {
                searchmemes_collapsible.visibility = View.VISIBLE
                activity?.tabs?.visibility = View.VISIBLE
                positiveScrolled = false
            } else {
                searchmemes_collapsible.visibility = View.GONE
                activity?.tabs?.visibility = View.GONE
                positiveScrolled = true
            }
        }
    }

    fun setupSearchRV(root: View){
        val application = requireNotNull(this.activity)
        val recyclerView = root.findViewById<RecyclerView>(R.id.searchmemes_recyclerview)

        //Thanks to https://antonioleiva.com/recyclerview-listener/
        adapter =
            SearchRV(application) { item, share ->
                when (share) {
                    0 -> memeCallback.onMemeClicked(item.filepath)
                    1 -> {
                        memeCallback.onMemeShared(item.filepath)
                        addUsageHistory(item.filepath, USAGE_HISTORY_ACTIONS.getOrDefault("share", 2), 1)
                    }
                    else -> memeCallback.onMemeInfoClicked(item)
                }
            }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        // Fixed the glitches...TTBOMK,  but now it fuks up the rounded corners of the layout.
        // Kinda fixed the corners with constraint offset, workaround till the below is achieved.
        // TODO: Figure out a way to add the rounded corners to foreground while staying transparent
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                // Can combine into one with && short circuiting but..... this looks cleaner
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (recyclerView.computeVerticalScrollOffset() == 0) {
                        toggleScrolled(true)
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                //Log.d(TAG, "Scrolled: $scrollOffset")
                // TODO: positiveScroll needed?
                if (!positiveScrolled || activity?.tabs?.visibility == View.VISIBLE) {
                    if (recyclerView.computeVerticalScrollOffset() > 0) {
                        toggleScrolled(false)
                    }
                }
            }
        })
    }

    fun setupSearchHistoryRV(root: View){
        val application = requireNotNull(this.activity)
        val recyclerView = root.findViewById<RecyclerView>(R.id.searchmemes_historyrv)

        //Thanks to https://antonioleiva.com/recyclerview-listener/
        val shAdapter =
            SearchHistoryRV(application) { item ->
                root.findViewById<SearchView>(R.id.searchmemes_search).setQuery(
                    item.pathOrQuery,
                    true
                )
                // onQueryTextSubmit(item.pathOrQuery)
            }
        recyclerView.adapter = shAdapter
        recyclerView.layoutManager = LinearLayoutManager(
            application,
            LinearLayoutManager.HORIZONTAL,
            false
        )
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
}