package com.legendbois.memeindexer.dialogs

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.legendbois.memeindexer.ConstantsHelper.TIME_RANGES
import com.legendbois.memeindexer.MiscHelper
import com.legendbois.memeindexer.R
import com.legendbois.memeindexer.viewmodel.UsageHistoryViewModel
import kotlinx.coroutines.launch

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
        val usageHistoryViewModel = ViewModelProvider(this).get(UsageHistoryViewModel::class.java)
        val spinner = view.findViewById<Spinner>(R.id.clearhistory_dropdown)
        val spinnerAdapter = ArrayAdapter<String>(view.context, android.R.layout.simple_spinner_item, TIME_RANGES.keys.toTypedArray())
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter

        view.findViewById<Button>(R.id.clearhistory_submit_button).setOnClickListener {
            val timeRangeKey = spinner.selectedItem.toString()
            val actionsToClear = mutableListOf<Int>()
            if(view.findViewById<SwitchCompat>(R.id.clearhistory_checkbox_search).isChecked) actionsToClear.add(1)
            if(view.findViewById<SwitchCompat>(R.id.clearhistory_checkbox_shared).isChecked) actionsToClear.add(2)
            lifecycleScope.launch {
                usageHistoryViewModel.deleteAfterTime(MiscHelper.getPastTimeFromKey(timeRangeKey), actionsToClear)
            }

            //Log.d(TAG, "Selected item $timeRangeKey, clear after time $clearAllAfterTime")
        }




    }
}