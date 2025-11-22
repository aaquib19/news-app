package com.example.newsapp.domain.usecase

import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.repository.NewsRepository
import javax.inject.Inject

class SaveArticleUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    /**
     * Saves an article to the local database.
     * The article's bookmarked status is not changed.
     */
    suspend operator fun invoke(article: Article) {
        repository.saveArticle(article) // We'll add this to the repository
    }
}