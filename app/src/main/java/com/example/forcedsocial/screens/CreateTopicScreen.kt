package com.example.forcedsocial.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.forcedsocial.auth.AuthViewModel
import com.example.forcedsocial.components.InputTextField
import com.example.forcedsocial.viewmodels.TopicViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun CreateTopicScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    parentTopicId: String?
) {
    val topicViewModel: TopicViewModel = viewModel()
    var content = ""
    val user = FirebaseAuth.getInstance().currentUser

    val topicCreationSuccess by topicViewModel.topicCreationSuccess.observeAsState()
    LaunchedEffect(topicCreationSuccess) {
        if (topicCreationSuccess == true) {
            navController.navigate("topics") {
                popUpTo("createTopic") { inclusive = true }
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
                    label = "Topic name",
                    onTextChange = { content = it }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = {
                    user?.let {
                        val userId = it.uid
                        topicViewModel.createTopic(userId, parentTopicId, content)
                    }
                }) {
                    Text(text = "Create")
                }
            }
        }
    }
}