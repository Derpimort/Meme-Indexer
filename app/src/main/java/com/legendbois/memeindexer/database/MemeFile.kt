package com.legendbois.memeindexer.database

import androidx.room.*
import com.legendbois.memeindexer.ConstantsHelper

@Fts4
@Entity(tableName = "meme_file_table")
data class MemeFile (
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "rowid") var rowid: Int = 0,
    @ColumnInfo(name = "filepath") val filepath: String,
    @ColumnInfo(name = "filename") val filename: String,
    @ColumnInfo(name = "ocrtext") var ocrtext: String? = ConstantsHelper.defaultText
    ){
    @Transient var memeIsSelected: Boolean = false
    @Transient var position: Int = 0
}