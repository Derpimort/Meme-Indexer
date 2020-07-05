package com.legendbois.memeindexer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.legendbois.memeindexer.database.MemeFile
import com.legendbois.memeindexer.database.MemeFileDao
import com.legendbois.memeindexer.database.MemeFilesDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class MemeFileViewModel(application: Application): AndroidViewModel(application) {
    private val database: MemeFileDao = MemeFilesDatabase.getDatabase(application).memeFileDao

    fun searchMemes(text: String): LiveData<List<MemeFile>>{
        return database.findByText(text)
    }

    fun searchUri(uri: String):List<MemeFile>{
        return database.findUri(uri)
    }

    fun insert(meme: MemeFile){
        database.insert(meme)
    }
    fun update(meme: MemeFile){
        database.update(meme)
    }
}