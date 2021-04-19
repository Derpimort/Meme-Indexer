package com.legendbois.memeindexer

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object MiscHelper {
    private val EMAIL_IDS = arrayOf("jatinsaini580@gmail.com")

    fun sendMail(context: Activity, emailSubject: String, emailText: String? = null){
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data= Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, EMAIL_IDS)
        intent.putExtra(Intent.EXTRA_SUBJECT,emailSubject)

        intent.putExtra(Intent.EXTRA_TEXT, emailText)
        try{
            context.startActivity(Intent.createChooser(intent,"Send Mail..."))
        }
        catch ( ex : android.content.ActivityNotFoundException){
            Toast.makeText(context.applicationContext,"There are no email clients installed", Toast.LENGTH_SHORT).show()
        }
    }
}