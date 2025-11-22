
package com.example.newsapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.usecase.BookmarkArticleUseCase
import com.example.newsapp.domain.usecase.GetBookmarkedArticlesUseCase
import com.example.newsapp.domain.usecase.UnbookmarkArticleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    private val getBookmarkedArticlesUseCase: GetBookmarkedArticlesUseCase,
    private val bookmarkArticleUseCase: BookmarkArticleUseCase,
    private val unbookmarkArticleUseCase: UnbookmarkArticleUseCase
) : ViewModel() {

    private val _articles = MutableStateFlow<PagingData<Article>>(PagingData.empty())
    val articles: StateFlow<PagingData<Article>> = _articles

    init {
        loadBookmarkedArticles()
    }

    /**
     * Loads the list of bookmarked articles from the database.
     */
    fun loadBookmarkedArticles() {
        viewModelScope.launch {
            getBookmarkedArticlesUseCase().cachedIn(viewModelScope).collect {
                _articles.value = it
            }
        }
    }

    /**
     * Toggles the bookmark status for an article to true.
     * Calls the BookmarkArticleUseCase.
     */
    fun onBookmarkClick(article: Article) {
        viewModelScope.launch {
            bookmarkArticleUseCase(article)
        }
    }

    /**
     * Toggles the bookmark status for an article to false.
     * Calls the UnbookmarkArticleUseCase.
     */
    fun onUnbookmarkClick(articleId: String) {
        viewModelScope.launch {
            unbookmarkArticleUseCase(articleId)
        }
    }
}