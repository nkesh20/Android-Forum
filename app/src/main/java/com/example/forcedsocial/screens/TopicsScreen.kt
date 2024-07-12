package com.example.forcedsocial.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.forcedsocial.auth.AuthViewModel
import com.example.forcedsocial.models.Topic
import com.example.forcedsocial.viewmodels.TopicViewModel


@Composable
fun TopicsScreen(authViewModel: AuthViewModel, navController: NavController) {
    val topicViewModel: TopicViewModel = viewModel()
    val topics = remember { mutableStateListOf<Topic>() }

    LaunchedEffect(Unit) {
        topicViewModel.getRealTimeTopics { updatedTopics ->
            topics.clear()
            topics.addAll(updatedTopics)
        }
    }

    val topicMap = topics.associateBy { it.id }

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
                        topics.filter { it.parentId == null }.forEach { rootTopic ->
                            TopicTree(topic = rootTopic, topicMap = topicMap, level = 0, authViewModel = authViewModel, navController = navController)
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun TopicTree(topic: Topic, topicMap: Map<String, Topic>, level: Int, authViewModel: AuthViewModel, navController: NavController) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        TopicItem(topic = topic, level = level, authViewModel = authViewModel, navController = navController)

        topicMap.values
            .filter { it.parentId == topic.id }
            .forEach { childTopic ->
                TopicTree(topic = childTopic, topicMap = topicMap, level = level + 1, authViewModel = authViewModel, navController = navController)
            }
    }
}

@Composable
fun TopicItem(topic: Topic, level: Int, authViewModel: AuthViewModel, navController: NavController) {
    val horizontalPadding = (8 + level * 16).dp
    val width = (1f - (level * 0.1f).coerceAtMost(0.5f)).coerceAtLeast(0.3f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = horizontalPadding, top = 4.dp, bottom = 4.dp, end = 8.dp)
            .fillMaxWidth(width)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(12.dp)
            .clickable {
                navController.navigate("posts/${topic.id}")
            }
    ) {
        Text(
            text = topic.name,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}