package com.legendbois.memeindexer

import android.app.Application
import com.squareup.picasso.Picasso

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        val picassoBuilder = Picasso.Builder(this)
        Picasso.setSingletonInstance(picassoBuilder.build())
    }
}