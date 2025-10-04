package com.example.plumproject.data.model

data class NewsArticle (
    val id: Int,
    val title: String,
    val description: String,
    val imageUrl: String? = null,
    // ADD THESE NEW FIELDS
    val tldrSummary: String? = null,
    val keyTakeaways: List<String>? = null
)