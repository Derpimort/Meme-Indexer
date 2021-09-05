package com.legendbois.memeindexer.dialogs

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.legendbois.memeindexer.ConstantsHelper
import com.legendbois.memeindexer.ConstantsHelper.TIME_RANGES
import com.legendbois.memeindexer.R
import java.lang.Long.max

class ClearHistoryDialogFragment() : DialogFragment(R.layout.popup_clearhistory) {

    companion object{
        private const val TAG = "ClearHistoryDialog"
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

        val spinner = view.findViewById<Spinner>(R.id.clearhistory_dropdown)
        val spinnerAdapter = ArrayAdapter<String>(view.context, android.R.layout.simple_spinner_item, TIME_RANGES.keys.toTypedArray())
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter

        view.findViewById<Button>(R.id.clearhistory_submit_button).setOnClickListener {
            val timeRangeKey = spinner.selectedItem.toString()
            val clearAllAfterTime = TIME_RANGES[timeRangeKey] ?: 0
            // max(0, System.currentTimeMillis() - clearAllAfterTime)

            //Log.d(TAG, "Selected item $timeRangeKey, clear after time $clearAllAfterTime")
        }




    }
}