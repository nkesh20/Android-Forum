package com.example.forcedsocial.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.forcedsocial.auth.AuthViewModel
import com.example.forcedsocial.models.Topic
import com.example.forcedsocial.viewmodels.TopicViewModel
import com.example.forcedsocial.viewmodels.UserViewModel


@Composable
fun TopicsScreen(authViewModel: AuthViewModel, navController: NavController) {
    val topicViewModel: TopicViewModel = viewModel()
    val userViewModel: UserViewModel = viewModel()

    val topics = remember { mutableStateListOf<Topic>() }

    LaunchedEffect(Unit) {
        topicViewModel.getRealTimeTopics { updatedTopics ->
            topics.clear()
            topics.addAll(updatedTopics)
        }
    }

    val topicMap = topics.associateBy { it.id }
    val canCreateTopic = userViewModel.isModerator(authViewModel.currentUser.value?.uid)

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
                            topics.filter { it.parentId.isNullOrEmpty() }
                                .forEach { rootTopic ->
                                    TopicTree(
                                        topic = rootTopic,
                                        topicMap = topicMap,
                                        level = 0,
                                        navController = navController,
                                        canCreateTopic = canCreateTopic
                                    )
                                }
                        }
                    }
                }
            }

            if (canCreateTopic) {
                FloatingActionButton(
                    onClick = { navController.navigate("createTopic") },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                        .offset(0.dp, (-10).dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Topic")
                }
            }
        }
    }
}

@Composable
fun TopicTree(
    topic: Topic,
    topicMap: Map<String, Topic>,
    level: Int,
    navController: NavController,
    canCreateTopic: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        TopicItem(
            topic = topic,
            level = level,
            navController = navController,
            canCreateTopic = canCreateTopic
        )

        topicMap.values
            .filter { it.parentId == topic.id }
            .forEach { childTopic ->
                TopicTree(
                    topic = childTopic,
                    topicMap = topicMap,
                    level = level + 1,
                    navController = navController,
                    canCreateTopic = canCreateTopic
                )
            }
    }
}

@Composable
fun TopicItem(
    topic: Topic,
    level: Int,
    canCreateTopic: Boolean,
    navController: NavController,
) {
    val horizontalPadding = (4 + level * 16).dp
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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = topic.name,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.weight(1f)
            )
            if (canCreateTopic) {
                IconButton(
                    onClick = { navController.navigate("createTopic?parentTopicId=${topic.id}") },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Subtopic",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}