package com.legendbois.memeindexer.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.legendbois.memeindexer.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class UsageHistoryViewModel(application: Application): AndroidViewModel(application) {
    private val database: UsageHistoryDao = MemeFilesDatabase.getDatabase(application).usageHistoryDao

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
                    duplicate.apply {
                        extraInfo = when(duplicate.actionId){
                            1, 2 -> duplicate.extraInfo?.plus(1)
                            else -> duplicate.extraInfo
                        }
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

    suspend fun deleteAfterTime(time: Long, actionIds: List<Int> = listOf(0,1,2)): Int {
        val deletableItems = database.findFilteredActionsAfter(time, actionIds)
        withContext(Dispatchers.IO) {
            for(deletable in deletableItems){
                database.delete(deletable)
            }
        }
        return deletableItems.size
    }
}