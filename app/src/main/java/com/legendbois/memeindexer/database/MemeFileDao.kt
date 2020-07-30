package com.legendbois.memeindexer.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MemeFileDao {
    @Query("Select rowid, filepath, filename from meme_file_table")
    suspend fun getAll(): List<MemeFile>

    @Query("Select rowid, filepath, filename from meme_file_table where filename LIKE :fname LIMIT :topn")
    suspend fun loadTopByFilename(fname: String, topn:Int =5): List<MemeFile>

    @Query("Select rowid, filepath, filename from meme_file_table where ocrtext LIKE :text LIMIT :topn")
    suspend fun loadTopByText(text: String, topn:Int =5): List<MemeFile>

    @Query("Select rowid, filepath, filename from meme_file_table where ocrtext LIKE :text OR filename LIKE :fname LIMIT :topn")
    suspend fun loadTopByText(text: String, fname: String, topn:Int =5): List<MemeFile>

    @Query("Select rowid, filepath, filename, ocrtext from meme_file_table where ocrtext LIKE :text")
    fun findByText(text: String): LiveData<List<MemeFile>>

    @Query("Select rowid, filepath, filename from meme_file_table where filepath LIKE :searchpath")
    fun findPath(searchpath: String): List<MemeFile>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg files: MemeFile)

    @Update
    fun update(vararg files: MemeFile)

    @Delete
    fun delete(file: MemeFile)
}