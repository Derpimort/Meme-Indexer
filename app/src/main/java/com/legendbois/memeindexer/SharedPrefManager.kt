package com.legendbois.memeindexer

import android.content.Context
import android.content.SharedPreferences

class SharedPrefManager private constructor(context: Context){

    init {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
    }

    var currentTheme: String
        get() = sharedPreferences.getString(KEY_CURRENT_THEME, ConstantsHelper.THEMES.keys.first()).toString()
        set(value) {
            val editor = sharedPreferences.edit()
            editor.putString(KEY_CURRENT_THEME, value)
            editor.apply()
        }

    companion object{
        private const val SHARED_PREF_NAME = "meme_indexer_settings"
        private const val KEY_CURRENT_THEME = "current_theme"

        private var mInstance: SharedPrefManager? = null
        private lateinit var sharedPreferences: SharedPreferences
        @Synchronized
        fun getInstance(context: Context): SharedPrefManager {
            if (mInstance == null) {
                mInstance = SharedPrefManager(context)
            }
            return mInstance as SharedPrefManager
        }
    }
}