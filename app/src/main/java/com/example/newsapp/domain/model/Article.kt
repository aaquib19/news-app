package com.example.newsapp.domain.model

import java.util.Date

data class Article(
    val id: String,
    val title: String,
    val description: String?,
    val content: String?,
    val author: String?,
    val source: String,
    val publishedAt: Date,
    val url: String,
    val imageUrl: String?,
    val isBookmarked: Boolean = false
)