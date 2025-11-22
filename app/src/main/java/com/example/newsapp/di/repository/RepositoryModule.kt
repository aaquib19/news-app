
package com.example.newsapp.di.repository

import com.example.newsapp.data.repository.NewsRepositoryImpl
import com.example.newsapp.domain.repository.NewsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideNewsRepository(

        apiService: com.example.newsapp.data.remote.api.NewsApiService,
        articleDao: com.example.newsapp.data.local.dao.ArticleDao,
        mapper: com.example.newsapp.data.ArticleMapper
    ): NewsRepository {
        return NewsRepositoryImpl(apiService, articleDao, mapper)
    }
}