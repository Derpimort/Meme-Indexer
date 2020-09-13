package com.legendbois.memeindexer.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UsageHistoryDao {

    @Query("SELECT * from usage_history_table where path_or_query LIKE :pathOrQuery")
    fun findPathOrQuery(pathOrQuery: String): List<UsageHistory>

    @Query("SELECT * from usage_history_table where actionId=2 ORDER BY extra_info DESC LIMIT 50")
    fun getSharedMemes(): LiveData<List<UsageHistory>>

    @Query("SELECT * FROM usage_history_table where actionId=1 ORDER BY modified_at, extra_info DESC LIMIT 50")
    fun getSearchedTerms(): LiveData<List<UsageHistory>>

    @Query("SELECT * FROM usage_history_table")
    suspend fun getAll(): List<UsageHistory>

    @Query("SELECT COUNT(id) FROM usage_history_table")
    fun getRowCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(action: UsageHistory)

    @Update
    fun update(action: UsageHistory)

    @Delete
    fun delete(action: UsageHistory)

    // Thanks to https://stackoverflow.com/a/50142304
    /*companion object{
        open class UsageHistoryDaoWrapper(private val daoInstance: UsageHistoryDao){
            fun insertWithTimestamp(action: UsageHistory){
                this@UsageHistoryDaoWrapper.daoInstance.insert(
                    action.apply {
                        createdAt = System.currentTimeMillis()
                        modifiedAt = System.currentTimeMillis()
                    }
                )
            }

            fun updateWithTimestamp(action: UsageHistory){
                this@UsageHistoryDaoWrapper.daoInstance.update(
                    action.apply {
                        modifiedAt = System.currentTimeMillis()
                    }
                )
            }
        }
    }*/

}