
package com.example.newsapp.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.newsapp.data.local.entities.ArticleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {

    @Query("SELECT * FROM articles WHERE isBookmarked = 1")
    fun getBookmarkedArticles(): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM articles WHERE id = :id")
    suspend fun getArticleById(id: String): ArticleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: ArticleEntity)

    @Update
    suspend fun updateArticle(article: ArticleEntity)

    @Query("UPDATE articles SET isBookmarked = 1 WHERE id = :id")
    suspend fun bookmarkArticle(id: String)

    @Query("UPDATE articles SET isBookmarked = 0 WHERE id = :id")
    suspend fun unbookmarkArticle(id: String)

    @Query("SELECT * FROM articles WHERE isBookmarked = 1 ORDER BY id DESC")
    fun getBookmarkedArticlesPagingSource(): PagingSource<Int, ArticleEntity>
}