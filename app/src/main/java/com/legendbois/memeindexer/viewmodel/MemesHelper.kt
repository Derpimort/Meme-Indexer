package com.legendbois.memeindexer.viewmodel

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.core.content.FileProvider
import com.legendbois.memeindexer.BuildConfig
import com.legendbois.memeindexer.MainActivity
import com.legendbois.memeindexer.R
import java.io.File

object MemesHelper {
    val legacyUriSdk = Build.VERSION_CODES.N_MR1
    fun shareImage(context: Context, filepath: String){
        val memeUri: Uri
        if(MainActivity.sdkVersion > legacyUriSdk){
            val memeFile = File(filepath)
            memeUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", memeFile)
        }
        else{
            memeUri = Uri.parse("file://$filepath")
        }

        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_STREAM, memeUri)
        shareIntent.type = "image/*"
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(Intent.createChooser(shareIntent, "Share Meme"))
    }

}