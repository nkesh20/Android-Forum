package com.example.forcedsocial.screens

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.forcedsocial.auth.AuthViewModel
import com.example.forcedsocial.components.PostCard
import com.example.forcedsocial.models.Post
import com.example.forcedsocial.models.User
import com.example.forcedsocial.viewmodels.RealTimeViewModel
import com.example.forcedsocial.viewmodels.UserViewModel

@Composable
fun PostListScreen(navController: NavController, authViewModel: AuthViewModel, topicId: String?) {
    val userViewModel: UserViewModel = viewModel()
    val realTimeUpdatesViewModel: RealTimeViewModel = viewModel()
    val posts = remember { mutableStateListOf<Post>() }

    LaunchedEffect(Unit) {
        realTimeUpdatesViewModel.getRealTimePosts({ updatedPosts ->
            posts.clear()
            posts.addAll(updatedPosts)
        }, topicId = topicId)
    }

    BottomNavigationLayout(navController, authViewModel) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Top
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            posts.forEach { post ->
                                val userId = post.userId
                                val user = remember {
                                    mutableStateOf<User?>(null)
                                }

                                userViewModel.getUserProfile(userId).addOnSuccessListener {
                                    user.value = it.toObject(User::class.java)
                                }.addOnFailureListener {
                                    Log.e("Post User", "Error while getting User from post")
                                }

                                PostCard(
                                    userName = if (!user.value?.displayName.isNullOrEmpty()) user.value?.displayName
                                        ?: "" else post.userId,
                                    postText = post.content,
                                    userImageUri = if (!user.value?.profilePictureUrl.isNullOrEmpty()) Uri.parse(
                                        user.value?.profilePictureUrl
                                    ) else null,
                                    postImageUri = if (!post.imageUrl.isNullOrEmpty()) Uri.parse(
                                        post.imageUrl
                                    ) else null
                                )
                            }
                        }
                    }
                }
            }
            FloatingActionButton(
                onClick = { navController.navigate("createPost/${topicId}") },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .offset(0.dp, (-10).dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Post")
            }
        }
    }
}