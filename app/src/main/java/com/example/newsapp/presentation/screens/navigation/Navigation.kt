package com.example.newsapp.presentation.screens.navigation


import android.net.Uri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.newsapp.presentation.screens.article.ArticleDetailsScreen
import com.example.newsapp.presentation.screens.article.WebViewScreen
import com.example.newsapp.presentation.screens.feed.NewsFeedScreen
import com.example.newsapp.presentation.screens.search.SearchScreen
import com.example.newsapp.presentation.viewmodel.ArticleDetailsViewModel
import com.example.newsapp.presentation.viewmodel.BookmarksViewModel
import com.example.newsapp.presentation.viewmodel.FeedViewModel
import com.example.newsapp.presentation.viewmodel.SearchViewModel

sealed class Screen(val route: String) {
    object NewsFeed : Screen("newsFeed")
    object Bookmarks : Screen("bookmarks")

    object Search : Screen("search")
    object ArticleDetails : Screen("articleDetails/{articleId}") {

        fun createRoute(articleId: String) = "articleDetails/$articleId"
    }
}

fun NavGraphBuilder.setupNewsNavigation(navController: NavController) {
    composable(Screen.NewsFeed.route) {

        NewsFeedScreen(
            onNavigateToArticleDetails = { articleId ->
                navController.navigate(Screen.ArticleDetails.createRoute(articleId))
            },
            onNavigateToSearch = { navController.navigate(Screen.Search.route) },
        )
    }

    composable(Screen.Search.route) {
        val searchViewModel: SearchViewModel = hiltViewModel()
        SearchScreen(
            viewModel = searchViewModel,
            onNavigateUp = { navController.navigateUp() },
            onNavigateToArticleDetails = { articleId -> // The parameter is now a String
                navController.navigate(Screen.ArticleDetails.createRoute(articleId))
            }
        )
    }


    composable(
        route = Screen.ArticleDetails.route,
        arguments = listOf(navArgument("articleId") { type = NavType.StringType })
    ) { backStackEntry ->

        val articleId = backStackEntry.arguments?.getString("articleId") ?: return@composable


        val articleDetailsViewModel: ArticleDetailsViewModel = hiltViewModel()

        ArticleDetailsScreen(
            viewModel = articleDetailsViewModel,
            onNavigateUp = { navController.navigateUp() },
            onOpenFullArticle = { url ->
                navController.navigate("webview?url=${Uri.encode(url)}")
            }
        )
    }

    composable(
        route = "webview?url={url}",
        arguments = listOf(
            navArgument("url") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val url = backStackEntry.arguments?.getString("url") ?: ""
        WebViewScreen(
            url = Uri.decode(url),
            onNavigateUp = { navController.navigateUp() }
        )
    }
}