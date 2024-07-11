package com.example.forcedsocial.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.forcedsocial.auth.AuthViewModel
import com.example.forcedsocial.viewmodels.PostViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun CreatePostScreen(navController: NavController, authViewModel: AuthViewModel) {
    val postViewModel: PostViewModel = viewModel()
    val content = remember { mutableStateOf("") }
    val user = FirebaseAuth.getInstance().currentUser


    BottomNavigationLayout(navController, authViewModel) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                BasicTextField(
                    value = content.value,
                    onValueChange = { content.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    user?.let {
                        val userId = it.uid
                        postViewModel.createPost(userId, content.value, null)
                    }
                }) {
                    Text(text = "Post")
                }
            }
        }
    }
}