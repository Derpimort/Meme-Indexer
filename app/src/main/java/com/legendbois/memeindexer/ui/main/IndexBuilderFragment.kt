package com.legendbois.memeindexer.ui.main

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.legendbois.memeindexer.R
import com.legendbois.memeindexer.database.MemeFile
import com.legendbois.memeindexer.viewmodel.MemeFileViewModel
import kotlinx.android.synthetic.main.indexbuilder_frag.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.Closeable
import java.io.File
import java.util.*

// TODO: Foreground service for uninterrupted large scans
class IndexBuilderFragment: Fragment(), View.OnClickListener {
    private var progressNumber: Int = 0
    private var concurrentImages: Int = 0
    private lateinit var memeFileViewModel: MemeFileViewModel
    private var updateDuplicates: Boolean = false
    private lateinit var rootView: View

    companion object{
        const val TAG="IndexBuilderFragment"
        const val DIRECTORY_REQUEST_CODE=2
        val imagesRegex="image/.*".toRegex()
        fun newInstance(): IndexBuilderFragment{
            return IndexBuilderFragment()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.indexbuilder_frag, container, false)
        val button: FloatingActionButton = rootView.findViewById(R.id.indexbuilder_button)
        memeFileViewModel = ViewModelProvider(this).get(MemeFileViewModel::class.java)
        button.setOnClickListener(this)
        return rootView
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
                        lifecycleScope.launch {
                            whenStarted {
                                traverseDirectoryEntries(parentUri)
                            }
                            toggleButtonState(true)
                            concurrentImages = 0
                        }
                    }
                }
            }

        }
    }
    //Thanks to https://stackoverflow.com/questions/41096332/issues-traversing-through-directory-hierarchy-with-android-storage-access-framew
    suspend fun traverseDirectoryEntries(rootUri: Uri?){
        val contentResolver = activity!!.contentResolver
        var childrenUri: Uri = try {
            //for childs and sub child dirs
            DocumentsContract.buildChildDocumentsUriUsingTree(
                rootUri,
                DocumentsContract.getDocumentId(rootUri)
            )
        } catch (e: java.lang.Exception) {
            // for parent dir
            DocumentsContract.buildChildDocumentsUriUsingTree(
                rootUri,
                DocumentsContract.getTreeDocumentId(rootUri)
            )
        }

        // Keep track of our directory hierarchy
        val dirNodes: MutableList<Uri> = LinkedList()
        dirNodes.add(childrenUri)
        while (dirNodes.isNotEmpty()) {
            childrenUri = dirNodes.removeAt(0) // get the item from top
            Log.d(TAG, "node uri:  $childrenUri")
            val c: Cursor? = contentResolver.query(
                childrenUri,
                arrayOf(
                    DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                    DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                    DocumentsContract.Document.COLUMN_MIME_TYPE
                ),
                null,
                null,
                null
            )
            try {
                if (c!=null) {
                    while (c.moveToNext()) {
                        val docId: String = c.getString(0)
                        val name: String = c.getString(1)
                        val mime: String = c.getString(2)
                        //Log.d(TAG, "New File $docId, $name, ${c.getString(3)}")
                        if (imagesRegex.matches(mime)){
                            // Untested Workaround to get filepath, bad practice probably but android devs forced my hand... "Security reasons"
                            val docSplit = docId.split(":")
                            val storages: Array<out File> = context!!.getExternalFilesDirs(null)
                            var filepath = docSplit[1]
                            if("primary".equals(docSplit[0])) {
                                filepath = storages[0].absolutePath.split("Android/")[0] + filepath
                            }
                            else{
                                filepath = storages[1].absolutePath.split("Android/")[0] + filepath
                            }
                            Log.d(TAG, "FilePath $filepath")
                            val duplicates = memeFileViewModel.searchPath(filepath)
                            if (duplicates.isEmpty() || updateDuplicates) {
                                Log.d(TAG, "Empty $filepath $duplicates")
                                getImageText(filepath, name, duplicates)

                            }
                            while(concurrentImages>4){
                                delay(1000)
                            }
                        }
                        if (isDirectory(mime)) {
                            val newNode: Uri =
                                DocumentsContract.buildChildDocumentsUriUsingTree(rootUri, docId)
                            dirNodes.add(newNode)
                        }
                    }
                }
            } finally {
                closeQuietly(c)
            }
        }
        //Room funcs testing
        /*for (f in db.getAll()){
            Log.d(TAG, "TestingAll ${f.rowid}, ${f.filename}, ${f.fileuri}")
        }

        for (f in db.loadTopByFilename("%3000d80%")){
            Log.d(TAG, "TestingFilename ${f.rowid}, ${f.filename}, ${f.fileuri}")
        }

        for (f in db.loadTopByText("%maroon 5%")){
            Log.d(TAG, "TestingText ${f.rowid}, ${f.filename}, ${f.fileuri}")
        }*/

    }

    private fun toggleButtonState(value: Boolean){
        if(value){
            indexbuilder_progress.visibility=View.GONE
            indexbuilder_button.visibility=View.VISIBLE
            val snackbar = Snackbar.make(
                rootView,
                "Succesfully indexed $progressNumber files",
                Snackbar.LENGTH_INDEFINITE
            )
            snackbar.setAction("DISMISS") {
                snackbar.dismiss()
            }
            snackbar.setActionTextColor(Color.parseColor("#fe9a00"))
            snackbar.show()
        }
        else{
            indexbuilder_button.visibility=View.GONE
            indexbuilder_progress.visibility=View.VISIBLE
        }

        indexbuilder_button.isEnabled=value
        indexbuilder_button.isClickable=value
    }

    private fun updateProgressText(){
        progressNumber+=1
        indexbuilder_progressText?.text="$progressNumber"
    }

    // Util method to check if the mime type is a directory
    private fun isDirectory(mimeType: String): Boolean {
        return DocumentsContract.Document.MIME_TYPE_DIR == mimeType
    }
    // Util method to close a closeable
    private fun closeQuietly(closeable: Closeable?) {
        if (closeable != null) {
            try {
                closeable.close()
            } catch (re: RuntimeException) {
                throw re
            } catch (ignore: Exception) {
                // ignore exception
            }
        }
    }

    private fun getImageText(imagePath: String, name: String, duplicates: List<MemeFile>){
        try {
            concurrentImages += 1
            val imageBitmap = BitmapFactory.decodeFile(imagePath)
            val image: InputImage = InputImage.fromBitmap(imageBitmap, 0)
            val model = TextRecognition.getClient()
            model.process(image)
                .addOnSuccessListener { visionText ->
                    Log.v(
                    TAG,
                    "docId: $id, name: $name, text: ${visionText.text}, uri: $imagePath"
                )
                    val fileuri = imagePath.toString()

                    if (visionText.text.isNotBlank()) {
                        updateProgressText()
                        if (duplicates.isEmpty()) {
                            lifecycleScope.launch {
                                memeFileViewModel.insert(
                                    MemeFile(
                                        filename = name,
                                        filepath = imagePath.toString(),
                                        ocrtext = visionText.text.toLowerCase(Locale.ROOT)
                                    )
                                )
                            }

                        } else {
                            for (duplicate in duplicates) {
                                lifecycleScope.launch {
                                    memeFileViewModel.update(
                                        MemeFile(
                                            rowid = duplicate.rowid,
                                            filepath = duplicate.filepath,
                                            filename = name,
                                            ocrtext = visionText.text.toLowerCase(Locale.ROOT)
                                        )
                                    )
                                }

                            }
                        }

                    }

                }
                .addOnFailureListener { e ->
                    Toast.makeText(activity!!.applicationContext, e.message, Toast.LENGTH_SHORT)
                        .show()
                }
                .addOnCompleteListener {
                    concurrentImages -= 1
                }
        }
        catch (e: java.lang.Exception){
            concurrentImages-=1
        }

    }
}