package com.example.newsapp.data

import com.example.newsapp.data.local.entities.ArticleEntity
import com.example.newsapp.data.remote.dto.ArticleDto
import com.example.newsapp.domain.model.Article
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ArticleMapper {

    fun mapToDomain(dto: ArticleDto): Article {
        return Article(
            id = dto.url.hashCode().toString(), // Use URL hash as a unique ID
            title = dto.title,
            description = dto.description,
            content = dto.content,
            author = dto.author,
            source = dto.source.name,
            publishedAt = parseDate(dto.publishedAt),
            url = dto.url,
            imageUrl = dto.urlToImage
        )
    }

    fun mapToEntity(domain: Article): ArticleEntity {
        return ArticleEntity(
            id = domain.id,
            title = domain.title,
            description = domain.description,
            content = domain.content,
            author = domain.author,
            source = domain.source,
            publishedAt = formatDate(domain.publishedAt), // Convert Date back to String for DB
            url = domain.url,
            imageUrl = domain.imageUrl,
            isBookmarked = domain.isBookmarked
        )
    }

    fun mapToDomain(entity: ArticleEntity): Article {
        return Article(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            content = entity.content,
            author = entity.author,
            source = entity.source,
            publishedAt = parseDate(entity.publishedAt), // Convert date string from DB back to a Date object
            url = entity.url,
            imageUrl = entity.imageUrl,
            isBookmarked = entity.isBookmarked
        )
    }

    private fun parseDate(dateString: String): Date {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        return format.parse(dateString) ?: Date()
    }

    private fun formatDate(date: Date): String {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        return format.format(date)
    }
}