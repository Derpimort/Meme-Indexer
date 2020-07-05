package com.legendbois.memeindexer.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [MemeFile::class], version = 3)
abstract class MemeFilesDatabase: RoomDatabase() {
    abstract val memeFileDao: MemeFileDao

    companion object{
        val DB_NAME="meme_files"
        @Volatile
        private var INSTANCE: MemeFilesDatabase? = null

        fun getDatabase(context: Context): MemeFilesDatabase{
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MemeFilesDatabase::class.java,
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