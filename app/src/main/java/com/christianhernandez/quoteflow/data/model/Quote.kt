package com.christianhernandez.quoteflow.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "quotes")
data class Quote(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val text: String,
    val author: String,
    val category: String,
    val lang: String = "en",
    val isSaved: Boolean = false,
    val savedAt: Long? = null,
)
