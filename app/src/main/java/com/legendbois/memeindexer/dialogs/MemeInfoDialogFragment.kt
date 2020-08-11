package com.legendbois.memeindexer.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
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
        /*Log.d(TAG, "Rowid: ${arguments?.getInt(ARG_ROWID)}\n" +
                "Filename: ${arguments?.getString(ARG_NAME)}\n" +
                "Filepath: ${arguments?.getString(ARG_PATH)}\n" +
                "OCRtext: ${arguments?.getString(ARG_OCRTEXT)}")*/

        if(arguments!=null) {
            val rowid = arguments!!.getInt(ARG_ROWID)
            val filename = arguments!!.getString(ARG_NAME)
            val filepath = arguments!!.getString(ARG_PATH)
            val ocrtext = arguments!!.getString(ARG_OCRTEXT)
            return activity?.let {
                val builder = AlertDialog.Builder(it, R.style.AlertDialogBase)
                val inflater = it.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val layout = inflater.inflate(R.layout.popup_memeinfo, null)
                layout.findViewById<ImageView>(R.id.memeinfo_title_image).setImageBitmap(BitmapFactory.decodeFile(filepath))
                layout.findViewById<TextView>(R.id.memeinfo_title_filename).text = filename
                layout.findViewById<TextView>(R.id.memeinfo_filepath).text = filepath
                layout.findViewById<TextView>(R.id.memeinfo_ocrtext).text = ocrtext
                layout.findViewById<ImageButton>(R.id.memeinfo_title_close).setOnClickListener {
                    dismiss()
                }
                builder.setView(layout)

                // Create the AlertDialog object and return it
                builder.create()
            } ?: throw IllegalStateException("Activity cannot be null")
        }

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage("Empty dialog")
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