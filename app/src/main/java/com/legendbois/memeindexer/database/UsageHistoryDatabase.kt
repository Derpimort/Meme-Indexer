package com.legendbois.memeindexer.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UsageHistory::class], version = 2)
abstract class UsageHistoryDatabase: RoomDatabase() {
    abstract val usageHistoryDao: UsageHistoryDao

    companion object{
        val DB_NAME="usage_history"
        @Volatile
        private var INSTANCE: UsageHistoryDatabase? = null

        fun getDatabase(context: Context): UsageHistoryDatabase{
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UsageHistoryDatabase::class.java,
                    DB_NAME
                )   .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }

}