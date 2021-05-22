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

    var scanBool: Boolean
    get() = sharedPreferences.getBoolean(KEY_SCAN_BOOL, false)
    set(value) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEY_SCAN_BOOL, value)
        editor.apply()
    }

    var scanTime: String
    get() = sharedPreferences.getString(KEY_SCAN_TIME, "03:00").toString()
    set(value) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_SCAN_TIME, value)
        editor.apply()
    }

    companion object{
        private const val SHARED_PREF_NAME = "meme_indexer_settings"
        //TODO: bad coding, find alternative to get string resource here
        const val KEY_CURRENT_THEME = "current_theme"
        const val KEY_SCAN_BOOL = "scan_bool"
        const val KEY_SCAN_TIME = "scan_time"

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