package com.example.newsapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String?,
    val content: String?,
    val author: String?,
    val source: String,
    val publishedAt: String,
    val url: String,
    val imageUrl: String?,
    val isBookmarked: Boolean = false
)