package com.example.forcedsocial.screens

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.forcedsocial.auth.AuthViewModel
import com.example.forcedsocial.components.InputTextField
import com.example.forcedsocial.components.RegularImageUpload
import com.example.forcedsocial.viewmodels.PostViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun CreatePostScreen(navController: NavController, authViewModel: AuthViewModel, topicId: String) {
    val postViewModel: PostViewModel = viewModel()
    val content = remember { mutableStateOf("") }
    val user = FirebaseAuth.getInstance().currentUser

    var imageUri: Uri? = null
    val context = LocalContext.current

    val postCreationSuccess by postViewModel.postCreationSuccess.observeAsState()
    LaunchedEffect(postCreationSuccess) {
        if (postCreationSuccess == true) {
            navController.navigate("posts") {
                popUpTo("createPost") { inclusive = true }
            }
        }
    }

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
                        imageUri = it
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = {
                    user?.let {
                        val userId = it.uid
                        postViewModel.createPost(userId, content.value, imageUri, topicId, context)
                    }
                }) {
                    Text(text = "Post")
                }
            }
        }
    }
}