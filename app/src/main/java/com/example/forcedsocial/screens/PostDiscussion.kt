package com.example.forcedsocial.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.forcedsocial.components.CommentInput
import com.example.forcedsocial.components.PostCard
import com.example.forcedsocial.models.Comment
import com.example.forcedsocial.models.Post
import com.example.forcedsocial.viewmodels.CommentViewModel
import com.example.forcedsocial.viewmodels.PostViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun PostDiscussion() {
    val context = LocalContext.current
    val postViewModel: PostViewModel = viewModel()
    val commentViewModel: CommentViewModel = viewModel()

    val postId = "" // TODO
    var post = remember<Post?> {
        null
    }
    var comments = remember<List<Comment>?> {
        null
    }

    if (postId != null) {
        Log.i("Post Discussion", postId)
    }

    Log.i("Post Discussion", post.toString())
    comments?.toString()?.let { Log.i("Post Discussion comments", it) }

    LaunchedEffect(Unit) {
        if (postId != null) {
            postViewModel.getPostById(postId = postId).addOnSuccessListener {
                post = it.toObject(Post::class.java)
            }.addOnFailureListener {
                Toast.makeText(
                    context,
                    "Couldn't get the post",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    LaunchedEffect(Unit) {
        if (postId != null) {
            commentViewModel.getRealTimeCommentsForPost(postId, onUpdate = {
                comments = it
            })
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            item {
                PostCard(userName = post?.userId?: "", postText = post?.content?: "", onClick = {})
                CommentInput(userName = post?.userId?: "", onCommentSubmit = {})

                comments?.forEach {comment ->
                    PostCard(userName = comment.userId, postText = comment.content, onClick = {})
                }
            }
        }
    }
}