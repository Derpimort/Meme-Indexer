package com.legendbois.memeindexer.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.legendbois.memeindexer.R

class IndexBuilderFragment: Fragment(), View.OnClickListener {
    companion object{
        const val TAG="IndexBuilderFragment"
        const val DIRECTORY_REQUEST_CODE=2

        fun newInstance(): IndexBuilderFragment{
            return IndexBuilderFragment()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.indexbuilder_frag, container, false)
        val button: FloatingActionButton = root.findViewById(R.id.indexbuilder_button)
        button.setOnClickListener(this)
        return root
    }

    override fun onClick(v: View?) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        startActivityForResult(Intent.createChooser(intent, "Choose directory"), DIRECTORY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if(requestCode == DIRECTORY_REQUEST_CODE){
                Log.d(TAG, data?.data.toString())
            }

        }
    }
}