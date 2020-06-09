package com.legendbois.memeindexer.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.legendbois.memeindexer.R
import kotlinx.android.synthetic.main.test_imageview.*

class SelectorTestFragment: Fragment(), View.OnClickListener {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.test_imageview, container, false)
        val button: Button = root.findViewById(R.id.test_image_button)
        button.setOnClickListener(this)
        return root
    }
    companion object {
        const val IMAGE_REQUEST_CODE=1
        //Better practice. Use template from PlaceholderFragment to handle args
        fun newInstance(): SelectorTestFragment {
            return SelectorTestFragment()
        }
    }

    override fun onClick(v: View?) {
        val intent=Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type="image/*"
        startActivityForResult(intent, IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if((resultCode == Activity.RESULT_OK) and (requestCode == IMAGE_REQUEST_CODE)){
            test_image_imageView.setImageURI(data?.data)
        }
    }
}