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
        return database.getSharedMemes()
    }
    fun getSearchedTerms(): LiveData<List<UsageHistory>>{
        return database.getSearchedTerms()
    }
    fun searchPathOrQuery(pathOrQuery: String):List<UsageHistory>{
        return database.findPathOrQuery(pathOrQuery)
    }

    fun getCount(): Int{
        return database.getRowCount()
    }

    suspend fun getAll(): List<UsageHistory>{
        return database.getAll()
    }

    suspend fun insert(action: UsageHistory) {
        withContext(Dispatchers.IO) {
            val duplicates = database.findPathOrQuery(action.pathOrQuery)
            if(duplicates.isEmpty()){
                database.insert(action.apply {
                    createdAt = System.currentTimeMillis()
                    modifiedAt = System.currentTimeMillis()
                })
            }
            else{
                for(duplicate in duplicates){
                    when(duplicate.actionId){
                        1 or 2 -> duplicate.extraInfo = duplicate.extraInfo?.plus(1)
                        else -> null
                    }
                    update(duplicate)
                }
            }
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