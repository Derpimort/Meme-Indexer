package com.legendbois.memeindexer


import android.content.res.Resources
import android.graphics.Color
import android.util.TypedValue
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

    fun getColorFromAttr(attr: Int): Int {
        val typedValue = TypedValue()
        var color = Color.TRANSPARENT
        theme?.let{
            if(it.resolveAttribute(attr, typedValue, true))
                color = typedValue.data
        }
        return color
    }
}