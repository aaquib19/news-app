package com.example.newsapp.presentation.screens.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.newsapp.R
import com.example.newsapp.domain.model.Article
import com.example.newsapp.presentation.screens.article.ArticleItemShimmer
import com.example.newsapp.presentation.screens.components.ArticleItem
import com.example.newsapp.presentation.viewmodel.BookmarksViewModel
import com.example.newsapp.presentation.viewmodel.FeedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsFeedScreen(
    feedViewModel: FeedViewModel = hiltViewModel(),
    bookmarksViewModel: BookmarksViewModel = hiltViewModel(),
    onNavigateToArticleDetails: (String) -> Unit,
    onNavigateToSearch: () -> Unit
) {

    val newsFeedPagingItems = feedViewModel.newsFeed.collectAsLazyPagingItems()
    val bookmarkedPagingItems = bookmarksViewModel.articles.collectAsLazyPagingItems()

    var isRefreshing by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("For you", "Bookmarks")

    val currentPagingItems = if (selectedTab == 0) newsFeedPagingItems else bookmarkedPagingItems

    val onArticleClick: (Article) -> Unit = { article ->
        feedViewModel.onArticleClick(article, onNavigateToArticleDetails)
    }

    val onBookmarkClick: (Article) -> Unit = { article ->
        if (selectedTab == 0) {
            feedViewModel.onBookmarkClick(article)
        } else {
            bookmarksViewModel.onUnbookmarkClick(article.id)
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Normal,
                            fontSize = 32.sp,
                            letterSpacing = (-0.5).sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(onClick = onNavigateToSearch, modifier = Modifier.size(40.dp)) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = MaterialTheme.colorScheme.onSurface,
                            height = 1.dp
                        )
                    },
                    divider = {}
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            modifier = Modifier.padding(horizontal = 0.dp)
                        ) {
                            Text(
                                text = title,
                                modifier = Modifier.padding(vertical = 16.dp),
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (selectedTab == index)
                                    MaterialTheme.colorScheme.onSurface
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                fontWeight = if (selectedTab == index) FontWeight.Normal else FontWeight.Normal
                            )
                        }
                    }
                }

                HorizontalDivider(
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.12f)
                )
            }
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                currentPagingItems.refresh()
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LaunchedEffect(currentPagingItems.loadState.refresh) {
                if (currentPagingItems.loadState.refresh is LoadState.NotLoading) {
                    isRefreshing = false
                }
            }

            when (val refreshState = currentPagingItems.loadState.refresh) {
                is LoadState.Loading -> {
                    if (!isRefreshing) LoadingState()
                }

                is LoadState.Error -> {
                    isRefreshing = false
                    ErrorState(
                        message = refreshState.error.message ?: "Unable to load articles",
                        onRetry = { currentPagingItems.retry() }
                    )
                }

                is LoadState.NotLoading -> {
                    if (currentPagingItems.itemCount == 0) {
                        val emptyMessage = if (selectedTab == 1) "No bookmarked articles yet" else "No articles yet"
                        EmptyState(message = emptyMessage, onRefresh = { currentPagingItems.refresh() })
                    } else {
                        key(selectedTab) {
                            NewsFeedList(
                                lazyPagingItems = currentPagingItems,
                                onArticleClick = onArticleClick,
                                onBookmarkClick = onBookmarkClick
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NewsFeedList(
    lazyPagingItems: LazyPagingItems<Article>,
    onArticleClick: (Article) -> Unit,
    onBookmarkClick: (Article) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(
            count = lazyPagingItems.itemCount,
            key = lazyPagingItems.itemKey { it.id }
        ) { index ->
            val article = lazyPagingItems[index]
            if (article != null) {
                ArticleItem(
                    article = article,
                    onArticleClick = onArticleClick,
                )

                if (index < lazyPagingItems.itemCount - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.12f)
                    )
                }
            }
        }

        when (val appendState = lazyPagingItems.loadState.append) {
            is LoadState.Loading -> item { LoadingMoreIndicator() }
            is LoadState.Error -> item {
                LoadMoreErrorItem(
                    message = "Failed to load more articles",
                    onRetry = { lazyPagingItems.retry() }
                )
            }
            is LoadState.NotLoading -> {
                if (appendState.endOfPaginationReached && lazyPagingItems.itemCount > 0) {
                    item { EndOfListItem() }
                }
            }
        }
    }
}


@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(10) {
            ArticleItemShimmer()
            if (it < 9) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.12f)
                )
            }
        }
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.ErrorOutline,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Text(
                text = "Something went wrong",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            TextButton(onClick = onRetry) { Text("Try Again") }
        }
    }
}

@Composable
private fun EmptyState(message: String, onRefresh: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Pull down to refresh",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(onClick = onRefresh) { Text("Refresh") }
        }
    }
}

@Composable
private fun LoadingMoreIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            strokeWidth = 2.dp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun LoadMoreErrorItem(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
        TextButton(onClick = onRetry) { Text("Retry") }
    }
}

@Composable
private fun EndOfListItem(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "You're all caught up",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Normal
        )
    }
}