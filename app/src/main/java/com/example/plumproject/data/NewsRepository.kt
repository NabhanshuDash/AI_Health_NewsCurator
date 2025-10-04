package com.example.plumproject.data

import com.example.plumproject.BuildConfig
import com.example.plumproject.data.model.NewsArticle
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray

object NewsRepository {

    private var articleCache: List<NewsArticle> = emptyList()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    private val aiSummaryService = AISummaryService()

    suspend fun getNewsArticles(): List<NewsArticle> {
        if (articleCache.isEmpty()) {
            articleCache = withContext(Dispatchers.IO) {
                try {
                    val prompt = """
                        Generate a list of 12 recent, compelling health news headlines.
                        For each headline, provide a one-sentence description.
                        Return the result as a valid JSON array where each object has "id", "title", and "description".
                        The ID should be a unique number from 1 to 12.
                        Example: [{"id": 1, "title": "Headline", "description": "Description."}]
                    """.trimIndent()

                    val response = generativeModel.generateContent(prompt)
                    parseNewsArticles(response.text ?: "[]")
                } catch (e: Exception) {
                    e.printStackTrace()
                    emptyList()
                }
            }
        }
        return articleCache
    }

    // 2. Ensure this function exists
    fun getArticleById(id: Int): NewsArticle? {
        return articleCache.find { it.id == id }
    }

    private fun parseNewsArticles(jsonString: String): List<NewsArticle> {
        val articles = mutableListOf<NewsArticle>()
        try {
            val cleanedJsonString = jsonString.trim().removeSurrounding("```json", "```").trim()
            val jsonArray = JSONArray(cleanedJsonString)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                articles.add(
                    NewsArticle(
                        id = jsonObject.getInt("id"),
                        title = jsonObject.getString("title"),
                        description = jsonObject.getString("description"),
                        imageUrl = "https://picsum.photos/800/400?random=${jsonObject.getInt("id")}"
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return articles
    }

    suspend fun getArticleWithSummary(article: NewsArticle): NewsArticle {
        val summary = aiSummaryService.getSummary(article.title, article.description)
        return article.copy(
            tldrSummary = summary.tldr,
            keyTakeaways = summary.takeaways
        )
    }
}