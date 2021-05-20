package com.legendbois.memeindexer

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity


open class BaseActivity : AppCompatActivity() {
    override fun getTheme(): Resources.Theme? {
        val theme: Resources.Theme = super.getTheme()
        val currentTheme = SharedPrefManager.getInstance(this).currentTheme
        ConstantsHelper.THEMES[currentTheme]?.let {
            theme.applyStyle(it, true)
        }
        return theme
    }
}