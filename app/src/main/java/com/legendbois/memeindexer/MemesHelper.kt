package com.legendbois.memeindexer

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.core.content.FileProvider
import java.io.File

object MemesHelper {
    const val TAG = "MemesHelper"
    const val legacyUriSdk = Build.VERSION_CODES.M
    fun shareOrViewImage(context: Activity, filepath: String, intentShare: Boolean = true){
        val memeUri: Uri = getMemeUri(context, filepath)

        var intentTitle = "Share Meme"
        val shareIntent: Intent
        if (intentShare){
            shareIntent = getShareIntent(memeUri)
        }
        else{
            shareIntent = getViewIntent(memeUri)
            intentTitle = "View Meme"
        }

        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(Intent.createChooser(shareIntent, intentTitle))
    }

    fun imagePopup(context: Activity, filepath: String){
        //Toast.makeText(context, "Item clicked $fileuri", Toast.LENGTH_LONG).show()
        val imageDialog = AlertDialog.Builder(context, R.style.AlertDialogBase)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.popup_image, null)
        val image = layout.findViewById<ImageView>(R.id.popup_image_meme)
        image.setImageBitmap(BitmapFactory.decodeFile(filepath))
        imageDialog.setView(layout)
        imageDialog.setPositiveButton(
            "Share"
        ){ dialog, i ->
            shareOrViewImage(context, filepath)
        }

        imageDialog.setNegativeButton(
            R.string.return_button
        ) { dialog, which ->
            dialog.dismiss()
        }
        imageDialog.create()
        imageDialog.show()
    }

    fun getMemeUri(context: Activity, filepath: String): Uri {
        return if(MainActivity.sdkVersion > legacyUriSdk){
            val memeFile = File(filepath)
            FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", memeFile)
            // Log.d(TAG, "Using Fileprovider")
        } else{
            Uri.parse("file://$filepath")
        }
    }

    fun getShareIntent(memeUri: Uri): Intent {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_STREAM, memeUri)
        shareIntent.type = "image/*"
        return shareIntent
    }

    fun getViewIntent(memeUri: Uri): Intent {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_VIEW
        shareIntent.data = memeUri
        shareIntent.setDataAndType(memeUri, "image/*")
        return shareIntent
    }

    fun processQueryText(text: String): String {
        //Log.d(TAG, "Processed query text: $processedText")
        return text
            .lowercase()
            .trim(' ')
            .replace("  ", " ")
            .replace(" ", "* NEAR ")
            .plus("*")
    }


}