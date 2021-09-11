package com.legendbois.memeindexer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.legendbois.memeindexer.MemesHelper.processQueryText
import com.legendbois.memeindexer.database.MemeFile
import com.legendbois.memeindexer.database.MemeFileDao
import com.legendbois.memeindexer.database.MemeFilesDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class MemeFileViewModel(application: Application): AndroidViewModel(application) {
    private val database: MemeFileDao = MemeFilesDatabase.getDatabase(application).memeFileDao
    private val ftsSpecialChar = Regex("[^a-zA-Z0-9]")
    fun searchMemes(text: String): LiveData<List<MemeFile>>{
        val processedText = processQueryText(text)
        return database.findByTextNear(processedText)
    }

    fun searchPath(path: String, filename: String):List<Int>{
        return database.findPath(path, filename.replace(ftsSpecialChar, " "))
    }

    fun getMemesCount(): Int{
        return database.getRowCount()
    }

    suspend fun searchAndUpdate(rowId: Int, ocrtext: String){
        withContext(Dispatchers.IO) {
            val searched = database.findByRow(rowId)
            for (meme in searched) {
                database.update(
                    meme.apply {
                        this.ocrtext = ocrtext
                    }
                )
            }
        }
    }

    suspend fun insert(meme: MemeFile) {
        withContext(Dispatchers.IO) {
            database.insert(meme)
        }
    }

    suspend fun update(meme: MemeFile) {
        withContext(Dispatchers.IO) {
            database.update(meme)
        }
    }
}