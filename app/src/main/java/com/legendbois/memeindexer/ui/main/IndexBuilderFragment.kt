package com.legendbois.memeindexer.ui.main

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenResumed
import androidx.lifecycle.whenStarted
import com.google.android.material.snackbar.Snackbar
import com.legendbois.memeindexer.R
import com.legendbois.memeindexer.database.MemeFile
import com.legendbois.memeindexer.database.UsageHistory
import com.legendbois.memeindexer.viewmodel.MemeFileViewModel
import com.legendbois.memeindexer.viewmodel.UsageHistoryViewModel
import kotlinx.android.synthetic.main.indexbuilder_frag.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.Closeable
import java.io.File
import java.util.*

// TODO: Foreground service for uninterrupted large scans
class IndexBuilderFragment: Fragment(), View.OnClickListener {
    private var progressNumber: Int = 0
    private lateinit var memeFileViewModel: MemeFileViewModel
    private lateinit var usageHistoryViewModel: UsageHistoryViewModel
    private lateinit var rootView: View

    companion object{
        const val TAG="IndexBuilderFragment"
        const val DIRECTORY_REQUEST_CODE=2
        val imagesRegex="image/.*".toRegex()

        @JvmStatic
        fun newInstance(): IndexBuilderFragment{
            return IndexBuilderFragment()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.indexbuilder_frag, container, false)
        val button: Button = rootView.findViewById(R.id.indexbuilder_button)

        button.setOnClickListener(this)
        return rootView
    }

    override fun onClick(v: View?) {
        // TODO: Add check for path in editext. Simple function, makes sense.
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
                        val parentUri = data.data!!
                        var totalFiles: Int
                        memeFileViewModel = ViewModelProvider(this).get(MemeFileViewModel::class.java)
                        usageHistoryViewModel = ViewModelProvider(this).get(UsageHistoryViewModel::class.java)
                        toggleButtonState(false, parentUri.path)

                        lifecycleScope.launch {
                            showStartToast()
                            totalFiles = traverseDirectoryEntries(parentUri)

                            toggleButtonState(true, totalFiles = totalFiles)
                            writeToHistory(parentUri.path, totalFiles)
                        }
                    }
                }
            }

        }
    }
    //Thanks to https://stackoverflow.com/questions/41096332/issues-traversing-through-directory-hierarchy-with-android-storage-access-framew
    suspend fun traverseDirectoryEntries(rootUri: Uri?): Int{
        var totalFiles = 0
        val contentResolver = activity!!.contentResolver
        val storages: Array<out File> = context!!.getExternalFilesDirs(null)
        // val oreoSdk = Build.VERSION_CODES.O

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
            //Log.d(TAG, "node uri:  $childrenUri")
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
                        //Log.d(TAG, "New File $docId, $name, $childrenUri")
                        if (imagesRegex.matches(mime)){
                            totalFiles += 1
                            var filepath: String
                            // Tested (on <9.0) Workaround to get filepath, bad practice probably but android devs forced my hand... "Security reasons"\
                            try {
                                val docSplit = docId.split(":")
                                filepath = docSplit[1]
                                filepath = when {
                                    "primary" == docSplit[0] -> {
                                        storages[0].absolutePath.split("Android/")[0] + filepath
                                    }
                                    "raw" == docSplit[0] -> {
                                        filepath
                                    }
                                    else -> {
                                        storages[1].absolutePath.split("Android/")[0] + filepath
                                    }
                                }
                            }
                            catch (versionE: Exception){
                                val file = File(docId)
                                filepath = file.path.split(":")[1]
                            }

                            // Log.d(TAG, "FilePath $filepath")
                            val duplicates = memeFileViewModel.searchPath(filepath, name)
                            if (duplicates.isEmpty()) {
                                //Log.d(TAG, "Empty $filepath $duplicates")
                                writeFileToDb(filepath, name)

                            }
                        }
                        if (isDirectory(mime)) {
                            val newNode: Uri =
                                DocumentsContract.buildChildDocumentsUriUsingTree(rootUri, docId)
                            dirNodes.add(newNode)
                        }
                    }
                }
            }
            finally {
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
        return totalFiles
    }

    private fun toggleButtonState(value: Boolean, path: String? = "", totalFiles: Int = 0){
        if(value){
            indexbuilder_progressbar.visibility=View.GONE

            val snackbar = Snackbar.make(
                rootView,
                "Total $progressNumber out of $totalFiles images found in the directory will be scanned.",
                Snackbar.LENGTH_INDEFINITE
            )
            snackbar.setAction("DISMISS") {
                snackbar.dismiss()
                indexbuilder_button.text = getString(R.string.scan)
            }
            snackbar.setActionTextColor(ContextCompat.getColor(activity!!, R.color.colorAccent))
            snackbar.view.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.colorBackgroundLight))
            snackbar.show()
        }
        else{
            indexbuilder_progressbar.visibility=View.VISIBLE
            indexbuilder_button.text = "$progressNumber"
        }
        progressNumber = 0
        indexbuilder_button.isEnabled = value
        indexbuilder_button.isClickable = value
        indexbuilder_path.setText(path)
        indexbuilder_path.isFocusableInTouchMode = value
        indexbuilder_path.isFocusable = value

    }

    private fun updateProgressText(){
        progressNumber+=1
        indexbuilder_button?.text="$progressNumber"
    }

    private fun writeToHistory(path: String?, extraInfo: Int){
        if (!path.isNullOrEmpty()){
            lifecycleScope.launch {
                whenStarted {
                    usageHistoryViewModel.insert(
                        UsageHistory(
                            actionId = 0,
                            pathOrQuery = path,
                            extraInfo = extraInfo
                        )
                    )
                }
            }
        }

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

    private fun showStartToast(){
        Toast.makeText(context, "Finding images in the directory, please don't close the app.", Toast.LENGTH_LONG).show()
    }

    private suspend fun writeFileToDb(imagePath: String, name: String) {
        progressNumber += 1
        memeFileViewModel.insert(
            MemeFile(
                filename = name,
                filepath = imagePath
            )
        )
    }
}