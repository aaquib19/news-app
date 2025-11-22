package com.example.newsapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.usecase.BookmarkArticleUseCase
import com.example.newsapp.domain.usecase.GetNewsFeedUseCase
import com.example.newsapp.domain.usecase.SaveArticleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getNewsFeedUseCase: GetNewsFeedUseCase,
    private val saveArticleUseCase: SaveArticleUseCase,
    private val bookmarkArticleUseCase: BookmarkArticleUseCase
) : ViewModel() {

    /**
     * The Flow of PagingData for the news feed.
     * It's cached in the viewModelScope to survive configuration changes.
     */
    val newsFeed: Flow<PagingData<Article>> = getNewsFeedUseCase()
        .cachedIn(viewModelScope)

    /**
     * Saves the article to the database and then triggers navigation.
     * This ensures the article is available for the details screen.
     */
    fun onArticleClick(article: Article, onNavigate: (String) -> Unit) {
        viewModelScope.launch {
            saveArticleUseCase(article)
            onNavigate(article.id)
        }
    }

    /**
     * Bookmarks an article from the feed.
     */
    fun onBookmarkClick(article: Article) {
        viewModelScope.launch {
            if (!article.isBookmarked) {
                bookmarkArticleUseCase(article)
            }

        }
    }
}