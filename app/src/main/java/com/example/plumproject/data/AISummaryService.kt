package com.example.plumproject.data

import kotlinx.coroutines.delay
import com.example.plumproject.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AISummaryService {

    data class ArticleSummary(
        val tldr: String,
        val takeaways: List<String>
    )

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )
    touch .gitignore
    suspend fun getSummary(articleTitle: String, articleDescription: String): ArticleSummary {
        return withContext(Dispatchers.IO) {
            try {
                val prompt = """
                    Summarize the following news article.
                    Title: "$articleTitle"
                    Description: "$articleDescription"
                    
                    Provide a response in JSON format with two keys:
                    1. "tldr": A 2-line "Too Long; Didn't Read" summary.
                    2. "takeaways": A JSON array of exactly 3 key takeaway bullet points.
                """.trimIndent()

                val response = generativeModel.generateContent(prompt)
                parseSummary(response.text ?: "{}")
            } catch (e: Exception) {
                e.printStackTrace()
                // Return a default summary on error
                ArticleSummary("Could not generate summary.", emptyList())
            }
        }
    }

    private fun parseSummary(jsonString: String): ArticleSummary {
        val cleanedJsonString = jsonString.trim().removeSurrounding("```json", "```").trim()
        val jsonObject = JSONObject(cleanedJsonString)
        val tldr = jsonObject.optString("tldr", "Summary not available.")
        val takeawaysArray = jsonObject.optJSONArray("takeaways")
        val takeaways = mutableListOf<String>()
        if (takeawaysArray != null) {
            for (i in 0 until takeawaysArray.length()) {
                takeaways.add(takeawaysArray.getString(i))
            }
        }
        return ArticleSummary(tldr, takeaways)
    }
}
//class AISummaryService {
//
//    // A data class to model the AI's response
//    data class ArticleSummary(
//        val tldr: String,
//        val takeaways: List<String>
//    )
//
//    // Simulate calling an AI model
//    suspend fun getSummary(articleId: Int, articleText: String): ArticleSummary {
//        // Pretend the AI is processing for 1.5 seconds
//        delay(1500)
//
//        // Return a hardcoded, mock summary based on the article ID
//        return when (articleId) {
//            1 -> ArticleSummary(
//                tldr = "Daily walking is crucial for heart health. Even short, consistent walks can significantly reduce the risk of cardiovascular diseases.",
//                takeaways = listOf(
//                    "Improves blood circulation.",
//                    "Lowers blood pressure.",
//                    "Strengthens the heart muscle."
//                )
//            )
//            2 -> ArticleSummary(
//                tldr = "A new cancer therapy targets cancer cells more precisely, leading to fewer side effects compared to traditional chemotherapy.",
//                takeaways = listOf(
//                    "Reduces nausea and fatigue.",
//                    "Improves patient quality of life during treatment.",
//                    "Offers a promising alternative for sensitive patients."
//                )
//            )
//            else -> ArticleSummary(
//                tldr = "Improving sleep hygiene is a powerful, non-pharmacological way to boost mental well-being and cognitive function.",
//                takeaways = listOf(
//                    "Consistent sleep schedules are key.",
//                    "Avoid screens before bedtime.",
//                    "Better sleep reduces anxiety and improves focus."
//                )
//            )
//        }
//    }
//}