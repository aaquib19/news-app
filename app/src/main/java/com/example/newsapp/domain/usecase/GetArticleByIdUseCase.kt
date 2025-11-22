package com.example.newsapp.domain.usecase


import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.repository.NewsRepository
import javax.inject.Inject

/**
 * A use case that retrieves a single article by its unique ID.
 *
 * This class encapsulates the business logic for fetching a single article,
 * making it reusable and keeping the ViewModel clean. It's a suspending
 * function because it performs a database operation.
 */
class GetArticleByIdUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    /**
     * Retrieves an article from the repository.
     *
     * @param id The unique identifier of the article (e.g., its URL).
     * @return The [Article] object if found, otherwise null.
     */
    suspend operator fun invoke(id: String): Article? {
        return repository.getArticleById(id)
    }
}