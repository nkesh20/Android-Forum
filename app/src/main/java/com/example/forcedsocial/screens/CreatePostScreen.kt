package com.example.forcedsocial.screens

import androidx.compose.foundation.layout.*
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
import com.example.forcedsocial.components.InputTextField
import com.example.forcedsocial.components.RegularImageUpload
import com.example.forcedsocial.viewmodels.PostViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun CreatePostScreen(navController: NavController, authViewModel: AuthViewModel) {
    val postViewModel: PostViewModel = viewModel()
    val content = remember { mutableStateOf("") }
    val user = FirebaseAuth.getInstance().currentUser

    val imageUri = remember { mutableStateOf(user?.photoUrl) }


    BottomNavigationLayout(navController, authViewModel) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                InputTextField(
                    label = "Write a post",
                    onTextChange = { content.value = it }
                )

                RegularImageUpload(
                    onUpload = {
                        imageUri.value = it
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = {
                    user?.let {
                        val userId = it.uid
                        postViewModel.createPost(userId, content.value, imageUri.value, "")
                    }
                }) {
                    Text(text = "Post")
                }
            }
        }
    }
}