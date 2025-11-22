
package com.example.newsapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.newsapp.data.local.dao.ArticleDao
import com.example.newsapp.data.local.entities.ArticleEntity
import com.example.newsapp.data.remote.api.NewsApiService
import com.example.newsapp.data.ArticleMapper
import com.example.newsapp.data.paging.SearchNewsPagingSource
import com.example.newsapp.data.remote.dto.ArticleDto
import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NewsRepositoryImpl(
    private val apiService: NewsApiService,
    private val articleDao: ArticleDao,
    private val mapper: ArticleMapper
) : NewsRepository {


    override fun getNewsFeed(): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { NewsPagingSource(apiService) }
        ).flow.map { pagingData  : PagingData<ArticleDto> ->

           pagingData.map { t -> mapper.mapToDomain(t) } }
    }



    override fun searchNews(query: String): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { SearchNewsPagingSource(apiService, query) }
        ).flow.map { pagingData  : PagingData<ArticleDto> ->

            pagingData.map { t -> mapper.mapToDomain(t) } }
    }

    override suspend fun getArticleById(id: String): Article? {
        val articleEntity = articleDao.getArticleById(id)
        return articleEntity?.let { mapper.mapToDomain(it) }
    }

    override suspend fun bookmarkArticle(article: Article) {
        val articleEntity = mapper.mapToEntity(article).copy(isBookmarked = true)
        articleDao.insertArticle(articleEntity)
    }

    override suspend fun unbookmarkArticle(articleId: String) {
        articleDao.unbookmarkArticle(articleId)
    }

    override fun getBookmarkedArticles(): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = {

                articleDao.getBookmarkedArticlesPagingSource()
            }
        ).flow.map { pagingData: PagingData<ArticleEntity> ->

            pagingData.map { entity -> mapper.mapToDomain(entity) }
        }
    }

    override suspend fun saveArticle(article: Article) {
        val articleEntity = mapper.mapToEntity(article)
        articleDao.insertArticle(articleEntity)
    }

}

