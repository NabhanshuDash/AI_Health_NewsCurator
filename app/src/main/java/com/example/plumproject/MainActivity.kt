package com.example.plumproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.plumproject.data.model.NewsArticle
import com.example.plumproject.viewmodel.NewsViewModel
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.plumproject.ui.theme.NewsDetailScreen


class MainActivity : ComponentActivity() {

    private val viewModel: NewsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                // 1. Create a NavController
                val navController = rememberNavController()

                // 2. Set up the NavHost, which is the container for all your screens
                NavHost(navController = navController, startDestination = "news_list") {

                    // 3. Define the "news_list" screen route
                    composable("news_list") {
                        NewsListScreen(
                            viewModel = viewModel,
                            onArticleClick = { articleId ->
                                // Navigate to the detail screen, passing the article's ID
                                navController.navigate("news_detail/$articleId")
                            }
                        )
                    }

                    // --- THIS BLOCK WAS UPDATED ---
                    // 4. Define the "news_detail" screen route with an argument
                    composable(
                        route = "news_detail/{articleId}",
                        arguments = listOf(navArgument("articleId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        // Extract the ID, ensuring it's not null
                        val articleId = backStackEntry.arguments?.getInt("articleId")
                        requireNotNull(articleId) { "Article ID is required" }

                        // Call your new detail screen
                        NewsDetailScreen(articleId = articleId, navController = navController)
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsListScreen(viewModel: NewsViewModel, onArticleClick: (Int) -> Unit) {
    val newsArticles by viewModel.newsArticles.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("AI Health News Curator") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(8.dp)
        ) {
            items(newsArticles) { article ->
                // Pass the click event up to the NavHost
                NewsItem(article = article, onArticleClick = onArticleClick)
            }
        }
    }
}

@Composable
fun NewsItem(article: NewsArticle, onArticleClick: (Int) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onArticleClick(article.id) }, // Make the whole card clickable
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            article.imageUrl?.let { imageUrl ->
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = article.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Text(
                text = article.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = article.description,
                fontSize = 14.sp
            )
        }
    }
}