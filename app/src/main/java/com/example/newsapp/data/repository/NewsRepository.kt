package com.example.newsapp.domain.repository

import androidx.paging.PagingData
import com.example.newsapp.data.remote.dto.ArticleDto
import com.example.newsapp.domain.model.Article
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    fun getNewsFeed(): Flow<PagingData<Article>>
    fun searchNews(query: String): Flow<PagingData<Article>>
    suspend fun getArticleById(id: String): Article?
    suspend fun bookmarkArticle(article: Article)
    suspend fun unbookmarkArticle(articleId: String)
    fun getBookmarkedArticles(): Flow<PagingData<Article>>
    suspend fun saveArticle(article: Article)

}