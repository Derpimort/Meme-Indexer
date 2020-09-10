package com.legendbois.memeindexer.database

import androidx.room.*

@Entity(tableName = "usage_history_table")
data class UsageHistory (
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "path_or_query") var pathOrQuery: String,
    @ColumnInfo(name = "created_at") var createdAt: Long? = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at") var modifiedAt: Long? = System.currentTimeMillis(),
    // 0 -> Scan, 1 -> Search, 2 -> Share
    @ColumnInfo(name = "actionId") var actionId: Int,
    @ColumnInfo(name = "extra_info") var extraInfo: String? = ""
)