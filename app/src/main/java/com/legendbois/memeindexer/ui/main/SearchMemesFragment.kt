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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.legendbois.memeindexer.R
import com.legendbois.memeindexer.viewmodel.MemeFileViewModel
import java.util.*


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

        //Thanks to https://antonioleiva.com/recyclerview-listener/
        adapter = SearchRVAdapter(application.applicationContext){ item, share ->
            if (share){
                shareImage(item.filepath)
            }
            else {
                imagePopup(item.filepath)
            }
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        memeFileViewModel = ViewModelProvider(this).get(MemeFileViewModel::class.java)
        search.isFocusable=false
        search.isIconifiedByDefault = false
        search.clearFocus()
        search.setOnQueryTextListener(this)
        return root
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            memeFileViewModel.searchMemes("%${query.toLowerCase(Locale.ROOT)}%").observe(this, Observer { memes ->
                adapter.setMemes(memes)
            })
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }

    fun shareImage(filepath: String){
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://$filepath"))
        shareIntent.type = "image/*"
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(Intent.createChooser(shareIntent, "Share Meme"))
    }

    // TODO: Make the Share button work
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
            "Return"
        ) { dialog, which ->
            dialog.dismiss()
        }
        imageDialog.create()
        imageDialog.show()

    }
}