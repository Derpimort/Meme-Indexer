package com.legendbois.memeindexer

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.legendbois.memeindexer.database.MemeFile
import com.legendbois.memeindexer.database.MemeFileDao
import com.legendbois.memeindexer.database.MemeFilesDatabase
import com.legendbois.memeindexer.ui.main.IndexBuilderFragment
import com.legendbois.memeindexer.ui.main.SectionsPagerAdapter
import com.legendbois.memeindexer.viewmodel.MemeFileViewModel
import kotlinx.android.synthetic.main.indexbuilder_frag.*
import kotlinx.coroutines.*
import java.io.Closeable
import java.util.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), IndexBuilderFragment.DataProcessor {
    private lateinit var model : TextRecognizer
    private lateinit var memeFileViewModel: MemeFileViewModel

    // From https://discuss.kotlinlang.org/t/unavoidable-memory-leak-when-using-coroutines/11603/9
//    override val coroutineContext: CoroutineContext
//        get() = Dispatchers.Main + Job()

    companion object{
        const val TAG = "MainActivity"
        val imagesRegex="image/[^g].*".toRegex()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        val fab: FloatingActionButton = findViewById(R.id.fab)
        if (!PermissionHelper.hasPermissions(this)) {
            //Log.d("APSIT","Permission checker")
            PermissionHelper.getPermissions(this)

        }
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PermissionHelper.PERMISSION_CODE) {
            if (!PermissionHelper.hasPermissions(this)) {
                Toast.makeText(this, "App needs camera and storage permissions", Toast.LENGTH_LONG) .show()
                finish()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }


    }

//    override fun onStop() {
//        super.onStop()
//        coroutineContext.cancelChildren()
//    }

    override fun onAttachFragment(fragment: Fragment){
        if(fragment is IndexBuilderFragment){
            fragment.setDataProcessor(this)
        }
    }

    override fun processData(parentUri: Uri): Boolean {
        model = TextRecognition.getClient()
        memeFileViewModel = ViewModelProvider(this).get(MemeFileViewModel::class.java)
        traverseDirectoryEntries(parentUri)
        Log.v(TAG, parentUri.toString())
        model.close()


        return true
    }

    //Thanks to https://stackoverflow.com/questions/41096332/issues-traversing-through-directory-hierarchy-with-android-storage-access-framew
    fun traverseDirectoryEntries(rootUri: Uri?){
        var progressNumber=0
        val contentResolver = contentResolver
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
                            Log.v(TAG, "$docId, $name, $mime")
                            getImageText(DocumentsContract.buildDocumentUriUsingTree(rootUri, docId), name)
                            runOnUiThread{
                                indexbuilder_progressText.text="$progressNumber"
                            }
                            progressNumber+=1
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

    private fun getImageText(imageUri: Uri, name: String){
        var inpimage : InputImage? = null
        try {
            inpimage = InputImage.fromFilePath(applicationContext, imageUri)

            model.process(inpimage)
                .addOnSuccessListener { visionText ->
                    Log.v(
                        TAG,
                        "name: $name, text: ${visionText.text}, uri: $imageUri"
                    )
                    val fileuri = imageUri.toString()
                    //updateProgressText()
                    if(visionText.text.isNotBlank()) {
                        val duplicates = memeFileViewModel.searchUri(fileuri)
                        if(duplicates.isEmpty()){
                            memeFileViewModel.insert(
                                MemeFile(
                                    filename = name,
                                    fileuri = imageUri.toString(),
                                    ocrtext = visionText.text.toLowerCase(Locale.ROOT)
                                )
                            )

                        }
                        else{
                            for(duplicate in duplicates){
                                    memeFileViewModel.update(
                                        MemeFile(
                                            rowid = duplicate.rowid,
                                            fileuri = duplicate.fileuri,
                                            filename = name,
                                            ocrtext = visionText.text.toLowerCase(Locale.ROOT)
                                        )
                                    )
                            }
                        }

                    }

                }
                .addOnFailureListener { e ->
                    Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
                }
                .addOnCompleteListener {
                    inpimage!!.bitmapInternal?.recycle()
                    inpimage = null
                }
        }
        catch (e: java.lang.Exception){
            inpimage?.bitmapInternal?.recycle()
            inpimage = null
            Log.e(TAG, e.localizedMessage)
        }
    }
}