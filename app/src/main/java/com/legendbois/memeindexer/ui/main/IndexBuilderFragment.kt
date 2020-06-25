package com.legendbois.memeindexer.ui.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import androidx.room.Room
import androidx.room.RoomDatabase
import com.google.android.gms.common.internal.FallbackServiceBroker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.legendbois.memeindexer.R
import com.legendbois.memeindexer.database.MemeFile
import com.legendbois.memeindexer.database.MemeFileDao
import com.legendbois.memeindexer.database.MemeFilesDatabase
import kotlinx.android.synthetic.main.indexbuilder_frag.*
import kotlinx.android.synthetic.main.test_imageview.*
import kotlinx.coroutines.*
import java.io.Closeable
import java.util.*
import kotlin.coroutines.CoroutineContext


class IndexBuilderFragment: Fragment(), View.OnClickListener {
    private var progressNumber: Int = -1
    internal lateinit var callback: DataProcessor

    fun setDataProcessor(callback: DataProcessor){
        this.callback = callback
    }

    interface DataProcessor{
        fun processData(parentUri: Uri): Boolean
    }
    //private lateinit var model : TextRecognizer
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
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                if (requestCode == DIRECTORY_REQUEST_CODE) {
                    if (this.context != null) {
                        toggleButtonState(false)
                        val parentUri = data.data
                        //model = TextRecognition.getClient()
                        lifecycleScope.launch {
                            whenStarted {
                                val complete = callback.processData(parentUri!!)
                            }
                            toggleButtonState(true)
                        }
                    }
                }

            }
        }
    }





    private fun toggleButtonState(value: Boolean){
        if(value){
            indexbuilder_progress.visibility=View.GONE
            indexbuilder_button.visibility=View.VISIBLE
        }
        else{
            indexbuilder_button.visibility=View.GONE
            indexbuilder_progress.visibility=View.VISIBLE
        }
        indexbuilder_button.isEnabled=value
        indexbuilder_button.isClickable=value
    }

    fun updateProgressText(){
        progressNumber+=1
        indexbuilder_progressText?.text="$progressNumber"
    }




}