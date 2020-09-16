package com.legendbois.memeindexer.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.BitmapFactory
import android.media.Image
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.legendbois.memeindexer.MemesHelper
import com.legendbois.memeindexer.R
import com.legendbois.memeindexer.database.MemeFile
import com.legendbois.memeindexer.viewmodel.MemeFileViewModel
import kotlinx.coroutines.launch

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
            return activity?.let { activity ->
                val builder = AlertDialog.Builder(activity, R.style.AlertDialogBase)
                val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val layout = inflater.inflate(R.layout.popup_memeinfo, null)
                layout.findViewById<ImageView>(R.id.memeinfo_title_image).setImageBitmap(BitmapFactory.decodeFile(filepath))
                layout.findViewById<TextView>(R.id.memeinfo_title_filename).text = filename
                layout.findViewById<TextView>(R.id.memeinfo_filepath).text = filepath
                val ocrtextView = layout.findViewById<TextView>(R.id.memeinfo_ocrtext)
                ocrtextView.text = ocrtext
                ocrtextView.movementMethod = ScrollingMovementMethod()
                layout.findViewById<ImageButton>(R.id.memeinfo_ocrtext_edit).setOnClickListener {
                    ocrtextEditDialog(it.context, rowid, ocrtext)
                }
                layout.findViewById<ImageButton>(R.id.memeinfo_title_close).setOnClickListener {
                    dismiss()
                }
                layout.findViewById<TextView>(R.id.memeinfo_filepath).setOnClickListener {
                    if (filepath != null){
                        MemesHelper.shareOrViewImage(it.context, filepath, false)
                    }

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


    // TODO: Base dialog for use in both feedback and this, reduce code repetition
    fun ocrtextEditDialog(context: Context, rowId: Int, ocrtext: String?){
        val alertDialog = AlertDialog.Builder(context, R.style.AlertDialogBase)
        val dialogView = layoutInflater.inflate(R.layout.popup_feedback, null)
        alertDialog.setTitle("Update OCRed Text")
        // Created a layout just in case
        alertDialog.setView(dialogView)
        val feedbackText = dialogView.findViewById<EditText>(R.id.feedback_text)
        feedbackText.hint = "OCR text"
        feedbackText.setText(ocrtext)

        alertDialog.setNegativeButton(
            "Cancel"
        ) { dialog, which ->
            dialog.dismiss()
        }

        alertDialog.setPositiveButton(
            "Update"
        ){ dialog, i ->

            if(!feedbackText.text.equals(ocrtext)){
                val memeFileViewModel = ViewModelProvider(this).get(MemeFileViewModel::class.java)
                lifecycleScope.launch {
                    memeFileViewModel.searchAndUpdate(rowId, feedbackText.text.toString())
                }
            }
        }
        alertDialog.create().show()


    }
}