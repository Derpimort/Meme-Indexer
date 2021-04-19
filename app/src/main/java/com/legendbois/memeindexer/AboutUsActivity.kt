package com.legendbois.memeindexer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_about_us.*


class AboutUsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)
        setSupportActionBar(aboutus_toolbar)

    }

    fun openSocial(view: View){
        val url = view.tag.toString()
        if(url == "email"){
            MiscHelper.sendMail(context = this,
                                emailSubject = "[Meme-Indexer Contact]:")
        }
        else {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.addCategory(Intent.CATEGORY_BROWSABLE)
            startActivity(intent)
        }
    }


}