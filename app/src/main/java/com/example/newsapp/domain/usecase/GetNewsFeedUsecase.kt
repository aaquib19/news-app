package com.example.newsapp.domain.usecase

import androidx.paging.PagingData
import com.example.newsapp.data.remote.dto.ArticleDto
import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNewsFeedUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    operator fun invoke(): Flow<PagingData<Article>> = repository.getNewsFeed()
}