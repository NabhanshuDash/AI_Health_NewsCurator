package com.example.plumproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.plumproject.data.NewsRepository
import com.example.plumproject.data.model.NewsArticle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// The repository is no longer needed in the constructor
class NewsDetailViewModel(private val articleId: Int) : ViewModel() {

    private val _articleDetail = MutableStateFlow<NewsArticle?>(null)
    val articleDetail: StateFlow<NewsArticle?> = _articleDetail

    init {
        loadArticleDetails()
    }

    private fun loadArticleDetails() {
        viewModelScope.launch {
            // Step 1: Get the base article from the repository's cache
            val baseArticle = NewsRepository.getArticleById(articleId)

            if (baseArticle != null) {
                // Step 2: Pass the full article object to get the summary
                val articleWithSummary = NewsRepository.getArticleWithSummary(baseArticle)
                _articleDetail.value = articleWithSummary
            }
        }
    }
}

// The factory is now simpler as it only needs to pass the articleId
class NewsDetailViewModelFactory(
    private val articleId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(NewsDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NewsDetailViewModel(articleId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}