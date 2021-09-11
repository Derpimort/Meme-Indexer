package com.legendbois.memeindexer.ui.main

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
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
        if ((resultCode == Activity.RESULT_OK) and (requestCode == IMAGE_REQUEST_CODE)) {
            val image_uri = data?.data
            if (image_uri != null){
                test_image_imageView.setImageURI(image_uri)
                getImageText(image_uri)
            }
        }
    }

    private fun getImageText(imageUri: Uri) {
        val image: InputImage = InputImage.fromFilePath(requireActivity().applicationContext, imageUri)
        val model = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
        model.process(image)
            .addOnSuccessListener { visionText ->
                test_image_text.text= visionText.text
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireActivity().applicationContext, e.message, Toast.LENGTH_SHORT).show()
            }
        labeler.process(image)
            .addOnSuccessListener { labels ->
                val texts = mutableListOf<String>()
                for (label in labels){
                    texts.add("${label.index}. ${label.text}\nConfidence: ${label.confidence}")
                    Log.d("MemeIndexer","${label.index}. ${label.text}\nConfidence: ${label.confidence}")
                }
                val adapter = ArrayAdapter(requireActivity().applicationContext, R.layout.simple_list, texts.toList())
                test_image_describe_list.adapter=adapter
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireActivity().applicationContext, e.message, Toast.LENGTH_SHORT).show()
            }
    }
}