package com.legendbois.memeindexer.database

import androidx.room.*

@Fts4
@Entity(tableName = "meme_file_table")
data class MemeFile (
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "rowid") var rowid: Int = 0,
    @ColumnInfo(name = "fileuri") val fileuri: String,
    @ColumnInfo(name = "filename") val filename: String,
    @ColumnInfo(name = "ocrtext") val ocrtext: String? = ""
    )