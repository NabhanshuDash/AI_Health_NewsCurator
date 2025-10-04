package com.example.plumproject.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.plumproject.data.NewsRepository
import com.example.plumproject.viewmodel.NewsDetailViewModel
import com.example.plumproject.viewmodel.NewsDetailViewModelFactory
import androidx.compose.material.icons.automirrored.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDetailScreen(
    articleId: Int,
    navController: NavController
) {
    // Create the ViewModel using its factory
    val viewModel: NewsDetailViewModel = viewModel(
        factory = NewsDetailViewModelFactory(articleId)
    )

    val article by viewModel.articleDetail.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Article Summary") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        // Show a loading indicator while the article and summary are being loaded
        if (article == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Once loaded, display the content in a scrollable column
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                article!!.imageUrl?.let {
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = article!!.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                    )
                }

                // Original Article Content
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = article!!.title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = article!!.description,
                        fontSize = 16.sp,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))

                    // AI Summary Section
                    Text(
                        "AI-Generated Summary",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // TL;DR
                    Text("TL;DR", fontWeight = FontWeight.SemiBold)
                    Text(
                        article!!.tldrSummary ?: "Summary not available.",
                        fontSize = 15.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Key Takeaways
                    Text("Key Takeaways", fontWeight = FontWeight.SemiBold)
                    article!!.keyTakeaways?.forEach { takeaway ->
                        Row(
                            modifier = Modifier.padding(start = 8.dp, top = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("•", modifier = Modifier.padding(end = 8.dp))
                            Text(takeaway, fontSize = 15.sp)
                        }
                    }
                }
            }
        }
    }
}