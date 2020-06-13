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
import androidx.room.Room
import androidx.room.RoomDatabase
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.legendbois.memeindexer.R
import com.legendbois.memeindexer.database.MemeFile
import com.legendbois.memeindexer.database.MemeFilesDatabase
import kotlinx.android.synthetic.main.test_imageview.*
import kotlinx.coroutines.launch
import java.io.Closeable
import java.util.*


class IndexBuilderFragment: Fragment(), View.OnClickListener {
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
                        val parentUri = data.data
                        val db = MemeFilesDatabase.getDatabase(this.context!!)
                        lifecycleScope.launch {
                            traverseDirectoryEntries(parentUri, db)
                        }

                        Log.d(TAG, data.data.toString())
                    }
                }

            }
        }
    }

    //Thanks to https://stackoverflow.com/questions/41096332/issues-traversing-through-directory-hierarchy-with-android-storage-access-framew
    suspend fun traverseDirectoryEntries(rootUri: Uri?, db: MemeFilesDatabase){
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
                        if (imagesRegex.matches(mime)){
                            getImageText(DocumentsContract.buildDocumentUriUsingTree(rootUri, docId), name, db)
                        }
                        if (isDirectory(mime)) {
                            val newNode: Uri =
                                DocumentsContract.buildChildDocumentsUriUsingTree(rootUri, docId)
                            dirNodes.add(newNode)
                        }
                    }
                }
            } finally {
                closeQuietly(c, db)
            }
        }
        for (f in db.memeFileDao.getAll()){
            Log.d(TAG, "TestingAll ${f.rowid}, ${f.filename}, ${f.fileuri}")
        }

        for (f in db.memeFileDao.loadTopByFilename("%3000d80%")){
            Log.d(TAG, "TestingFilename ${f.rowid}, ${f.filename}, ${f.fileuri}")
        }

        for (f in db.memeFileDao.loadTopByText("%maroon 5%")){
            Log.d(TAG, "TestingText ${f.rowid}, ${f.filename}, ${f.fileuri}")
        }
    }

    // Util method to check if the mime type is a directory
    private fun isDirectory(mimeType: String): Boolean {
        return DocumentsContract.Document.MIME_TYPE_DIR == mimeType
    }
    // Util method to close a closeable
    private fun closeQuietly(closeable: Closeable?, db: MemeFilesDatabase) {
        if (closeable != null) {
            try {
                closeable.close()
                //db.close()
            } catch (re: RuntimeException) {
                throw re
            } catch (ignore: Exception) {
                // ignore exception
            }
        }
    }

    private fun getImageText(imageUri: Uri, name: String, db: MemeFilesDatabase){
        val image: InputImage = InputImage.fromFilePath(activity!!.applicationContext, imageUri)
        val model = TextRecognition.getClient()
        model.process(image)
            .addOnSuccessListener { visionText ->
                /*Log.d(
                    TAG,
                    "docId: $id, name: $name, text: ${visionText.text}, uri: $imageUri"
                )
                if(visionText.text.isNotBlank()) {
                    db.memeFileDao.insert(
                        MemeFile(
                            filename = name,
                            fileuri = imageUri.toString(),
                            ocrtext = visionText.text.toLowerCase()
                        )
                    )
                }*/
            }
            .addOnFailureListener { e ->
                Toast.makeText(activity!!.applicationContext, e.message, Toast.LENGTH_SHORT).show()
            }
    }
}