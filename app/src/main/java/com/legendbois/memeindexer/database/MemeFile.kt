package com.legendbois.memeindexer.database

import androidx.room.*

@Fts4
@Entity(tableName = "meme_file_table")
data class MemeFile (
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "rowid") var rowid: Int = 0,
    @ColumnInfo(name = "filepath") val filepath: String,
    @ColumnInfo(name = "filename") val filename: String,
    @ColumnInfo(name = "ocrtext") var ocrtext: String? = "5PAC38AR_UN1ND3X3D_5P3C1F13R"
    )