package com.legendbois.memeindexer

import android.content.Context
import android.graphics.Typeface
import java.lang.reflect.Field


//From https://stackoverflow.com/a/33923946
object TypefaceUtil {
    /**
     * Using reflection to override default typeface
     * NOTICE: DO NOT FORGET TO SET TYPEFACE FOR APP THEME AS DEFAULT TYPEFACE WHICH WILL BE OVERRIDDEN
     *
     * @param context                    to work with assets
     * @param customFontFileNameInAssets file name of the font from assets
     */
    fun overrideFont(
        context: Context,
        customFontFileNameInAssets: String
    ) {
        val customFontTypeface =
            Typeface.createFromAsset(context.assets, customFontFileNameInAssets)
        val newMap: MutableMap<String, Typeface> =
            HashMap()
        newMap["sans"] = customFontTypeface
        try {
            val staticField: Field = Typeface::class.java
                .getDeclaredField("sSystemFontMap")
            staticField.isAccessible = true
            staticField.set(null, newMap)
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }
}