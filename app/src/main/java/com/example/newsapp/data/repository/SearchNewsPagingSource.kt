
package com.example.newsapp.data.paging // Recommended package name

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.newsapp.BuildConfig
import com.example.newsapp.data.remote.api.NewsApiService
import com.example.newsapp.data.remote.dto.ArticleDto
import retrofit2.HttpException // <-- Import HttpException
import java.io.IOException

class SearchNewsPagingSource(
    private val apiService: NewsApiService,
    private val query: String
) : PagingSource<Int, ArticleDto>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArticleDto> {
        return try {
            val page = params.key ?: 1
            val response = apiService.searchNews(
                query = query,
                page = page,
                pageSize = params.loadSize,
                apiKey = BuildConfig.NEWS_API_KEY
            )

            LoadResult.Page(
                data = response.articles,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.articles.isEmpty()) null else page + 1
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) { // <-- ADD THIS CATCH BLOCK
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ArticleDto>): Int? {

        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}