package com.legendbois.memeindexer.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import com.legendbois.memeindexer.R
import com.legendbois.memeindexer.database.MemeFile
import com.legendbois.memeindexer.ui.main.PlaceholderFragment

class MemeInfoDialogFragment : DialogFragment() {

    companion object{

        private const val ARG_NAME = "memefile_name"
        private const val ARG_PATH = "memefile_path"
        private const val ARG_OCRTEXT = "memefile_ocrtext"
        private const val ARG_ROWID = "memefile_rowid"
        private const val TAG = "MemeInfoDialog"

        @JvmStatic
        fun newInstance(memefile: MemeFile): MemeInfoDialogFragment{
            return MemeInfoDialogFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_ROWID, memefile.rowid)
                    putString(ARG_NAME, memefile.filename)
                    putString(ARG_PATH, memefile.filepath)
                    putString(ARG_OCRTEXT, memefile.ocrtext)
                }
            }

        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Log.d(TAG, "Rowid: ${arguments?.getInt(ARG_ROWID)}\n" +
                "Filename: ${arguments?.getString(ARG_NAME)}\n" +
                "Filepath: ${arguments?.getString(ARG_PATH)}\n" +
                "OCRtext: ${arguments?.getString(ARG_OCRTEXT)}")

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage("Testing dialog")
                .setNegativeButton(
                    R.string.return_button
                ) { dialog, id ->
                    dialog.dismiss()
                }
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}