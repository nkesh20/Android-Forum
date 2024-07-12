package com.example.forcedsocial.screens

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.Button
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.forcedsocial.auth.AuthViewModel
import com.example.forcedsocial.models.Post
import com.example.forcedsocial.viewmodels.RealTimeViewModel

@Composable
fun PostListScreen(navController: NavController, authViewModel: AuthViewModel) {
    val realTimeUpdatesViewModel: RealTimeViewModel = viewModel()
    val posts = remember { mutableStateListOf<Post>() }

    LaunchedEffect(Unit) {
        realTimeUpdatesViewModel.getRealTimePosts({ updatedPosts ->
            posts.clear()
            posts.addAll(updatedPosts)
        })
    }

    LazyColumn {
        item {
            BottomNavigationLayout(navController, authViewModel) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        posts.forEach { post ->
                            Text(text = post.content, modifier = Modifier.padding(8.dp))
                            if (!post.imageUrl.isNullOrEmpty()) {
                                Log.i("Post List screen", post.imageUrl)
                                AsyncImage(model = Uri.parse(post.imageUrl), contentDescription = "Image", modifier = Modifier.size(200.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { navController.navigate("createPost") }) {
                            Text(text = "Create Post")
                        }
                    }
                }
            }
        }
    }
}