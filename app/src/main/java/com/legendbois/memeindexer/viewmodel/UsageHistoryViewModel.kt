package com.legendbois.memeindexer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.legendbois.memeindexer.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class UsageHistoryViewModel(application: Application): AndroidViewModel(application) {
    private val database: UsageHistoryDao = UsageHistoryDatabase.getDatabase(application).usageHistoryDao

//    fun searchMemes(text: String): LiveData<List<MemeFile>> {
//        return database.findByText(text)
//    }
//
    fun getSharedMemes(): LiveData<List<UsageHistory>>{
        return database.findSharedMemes()
    }
    fun searchPathOrQuery(pathOrQuery: String):List<UsageHistory>{
        return database.findPathOrQuery(pathOrQuery)
    }

    fun getCount(): Int{
        return database.getRowCount()
    }

    suspend fun insert(action: UsageHistory) {
        withContext(Dispatchers.IO) {
            database.insert(action.apply {
                createdAt = System.currentTimeMillis()
                modifiedAt = System.currentTimeMillis()
            })
        }
    }

    suspend fun update(action: UsageHistory) {
        withContext(Dispatchers.IO) {
            database.update(action.apply {
                modifiedAt = System.currentTimeMillis()
            })
        }
    }
}