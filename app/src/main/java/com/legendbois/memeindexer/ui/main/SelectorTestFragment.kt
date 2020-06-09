package com.legendbois.memeindexer.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.legendbois.memeindexer.R

class SelectorTestFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_main, container, false)
        val textView: TextView = root.findViewById(R.id.section_label)
        textView.text=getString(R.string.test_hello)
        return root
    }
    companion object {
        //Better practice. Use template from PlaceholderFragment to handle args
        fun newInstance(): SelectorTestFragment {
            return SelectorTestFragment()
        }
    }
}