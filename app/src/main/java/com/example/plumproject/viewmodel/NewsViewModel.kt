package com.example.plumproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plumproject.data.NewsRepository
import com.example.plumproject.data.model.NewsArticle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NewsViewModel : ViewModel() {

    // 1. REMOVED: You no longer need a 'repository' variable
    // private val repository = NewsRepository()

    private val _newsArticles = MutableStateFlow<List<NewsArticle>>(emptyList())
    val newsArticles: StateFlow<List<NewsArticle>> = _newsArticles

    init {
        loadNews()
    }

    private fun loadNews() {
        viewModelScope.launch {
            // 2. UPDATED: Call the function directly on the NewsRepository object
            val articles = NewsRepository.getNewsArticles()
            _newsArticles.value = articles
        }
    }
}