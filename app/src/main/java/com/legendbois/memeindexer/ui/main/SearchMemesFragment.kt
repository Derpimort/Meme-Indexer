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
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.legendbois.memeindexer.R
import com.legendbois.memeindexer.database.MemeFile
import com.legendbois.memeindexer.database.UsageHistory
import com.legendbois.memeindexer.dialogs.MemeInfoDialogFragment
import com.legendbois.memeindexer.viewmodel.MemeFileViewModel
import com.legendbois.memeindexer.viewmodel.MemesHelper
import com.legendbois.memeindexer.viewmodel.UsageHistoryViewModel
import kotlinx.coroutines.launch
import java.util.*


class SearchMemesFragment: Fragment(), SearchView.OnQueryTextListener {
    private lateinit var memeFileViewModel: MemeFileViewModel
    private lateinit var usageHistoryViewModel: UsageHistoryViewModel
    private lateinit var adapter: SearchRVAdapter
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
        val application = requireNotNull(this.activity).application
        val recyclerView = root.findViewById<RecyclerView>(R.id.searchmemes_recyclerview)

        //Thanks to https://antonioleiva.com/recyclerview-listener/
        adapter = SearchRVAdapter(application.applicationContext){ item, share ->
            when(share){
                0-> imagePopup(item.filepath)
                1-> shareImage(item.filepath)
                else -> infoPopup(item)
            }
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        memeFileViewModel = ViewModelProvider(this).get(MemeFileViewModel::class.java)
        usageHistoryViewModel = ViewModelProvider(this).get(UsageHistoryViewModel::class.java)
        search.isFocusable=false
        search.isIconifiedByDefault = false
        search.clearFocus()
        search.setOnQueryTextListener(this)
        return root
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            view!!.findViewById<SearchView>(R.id.searchmemes_search).clearFocus()
            memeFileViewModel.searchMemes("%${query.toLowerCase(Locale.ROOT)}%").observe(this, Observer { memes ->
                adapter.setMemes(memes)
            })
            lifecycleScope.launch {
                usageHistoryViewModel.insert(
                    UsageHistory(
                        pathOrQuery = query,
                        actionId = 1
                    )
                )
            }
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }

    fun shareImage(filepath: String){
        if (context != null){
            MemesHelper.shareImage(context!!.applicationContext, filepath)
            lifecycleScope.launch {
                usageHistoryViewModel.insert(
                    UsageHistory(
                        pathOrQuery = filepath,
                        actionId = 2
                    )
                )
            }
        }

    }

    fun imagePopup(filepath: String){
        //Toast.makeText(context, "Item clicked $fileuri", Toast.LENGTH_LONG).show()
        val imageDialog = AlertDialog.Builder(context, R.style.AlertDialogBase)
        val inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.popup_image, null)
        val image = layout.findViewById<ImageView>(R.id.popup_image_meme)
        image.setImageBitmap(BitmapFactory.decodeFile(filepath))
        imageDialog.setView(layout)
        imageDialog.setPositiveButton(
            "Share"
        ){ dialog, i ->
            shareImage(filepath)
        }

        imageDialog.setNegativeButton(
            R.string.return_button
        ) { dialog, which ->
            dialog.dismiss()
        }
        imageDialog.create()
        imageDialog.show()

    }

    fun infoPopup(memefile: MemeFile){
        val dialog = MemeInfoDialogFragment.newInstance(memefile)
        dialog.show(parentFragmentManager, "meme_info")
    }
}