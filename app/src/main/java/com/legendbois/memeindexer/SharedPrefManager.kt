package com.legendbois.memeindexer

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class SharedPrefManager private constructor(context: Context){

    init {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
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
        //TODO: bad coding, find alternative to get string resource here
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