package com.legendbois.memeindexer.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.legendbois.memeindexer.R

class ClearHistoryDialogFragment() : DialogFragment(R.layout.popup_clearhistory) {

    companion object{
        fun newInstance(): ClearHistoryDialogFragment{
            return ClearHistoryDialogFragment().apply{
                // If ever args are needed
                arguments = Bundle()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<ImageButton>(R.id.clearhistory_title_close).setOnClickListener {
            dismiss()
        }




    }
}