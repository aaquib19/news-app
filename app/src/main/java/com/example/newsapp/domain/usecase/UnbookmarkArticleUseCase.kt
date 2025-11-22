package com.example.newsapp.domain.usecase

import com.example.newsapp.domain.repository.NewsRepository
import javax.inject.Inject

class UnbookmarkArticleUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    suspend operator fun invoke(articleId: String) {
        repository.unbookmarkArticle(articleId)
    }
}