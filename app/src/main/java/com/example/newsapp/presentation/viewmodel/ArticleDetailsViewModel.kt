
package com.example.newsapp.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.usecase.BookmarkArticleUseCase
import com.example.newsapp.domain.usecase.GetArticleByIdUseCase
import com.example.newsapp.domain.usecase.UnbookmarkArticleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticleDetailsViewModel @Inject constructor(
    private val getArticleByIdUseCase: GetArticleByIdUseCase,
    private val bookmarkArticleUseCase: BookmarkArticleUseCase, // Inject bookmark use case
    private val unbookmarkArticleUseCase: UnbookmarkArticleUseCase, // Inject unbookmark use case
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _article = MutableStateFlow<Article?>(null)
    val article: StateFlow<Article?> = _article

    private var currentArticleId: String? = null

    init {

        val articleId = savedStateHandle.get<String>("articleId")
        if (articleId != null) {
            currentArticleId = articleId
            loadArticle(articleId)
        }
    }

    private fun loadArticle(articleId: String) {
        viewModelScope.launch {
            _article.value = getArticleByIdUseCase(articleId)
        }
    }

    /**
     * Toggles the bookmark status of the currently displayed article.
     * It bookmarks if unbookmarked, and unbookmarks if bookmarked.
     * After the operation, it re-fetches the article to update the UI state.
     */
    fun onBookmarkClick() {
        val article = _article.value ?: return // Do nothing if article is null
        val articleId = currentArticleId ?: return // Do nothing if ID is null

        viewModelScope.launch {
            try {
                if (article.isBookmarked) {
                    unbookmarkArticleUseCase(articleId)
                } else {
                    bookmarkArticleUseCase(article)
                }

                loadArticle(articleId)
            } catch (e: Exception) {

                e.printStackTrace()
            }
        }
    }
}