package com.legendbois.memeindexer.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MemeFileDao {
    @Query("Select rowid, fileuri, filename from meme_file_table")
    suspend fun getAll(): List<MemeFile>

    @Query("Select rowid, fileuri, filename from meme_file_table where filename LIKE :fname LIMIT :topn")
    suspend fun loadTopByFilename(fname: String, topn:Int =5): List<MemeFile>

    @Query("Select rowid, fileuri, filename from meme_file_table where ocrtext LIKE :text LIMIT :topn")
    suspend fun loadTopByText(text: String, topn:Int =5): List<MemeFile>

    @Query("Select rowid, fileuri, filename from meme_file_table where ocrtext LIKE :text OR filename LIKE :fname LIMIT :topn")
    suspend fun loadTopByText(text: String, fname: String, topn:Int =5): List<MemeFile>

    @Insert
    fun insert(vararg files: MemeFile)

    @Delete
    fun delete(file: MemeFile)
}