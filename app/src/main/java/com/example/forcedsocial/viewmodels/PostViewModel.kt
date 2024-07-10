package com.example.forcedsocial.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forcedsocial.models.Post
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class PostViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    fun createPost(userId: String, content: String, imageUrl: String?) {
        if (content.isEmpty()) {
            return
        }
        viewModelScope.launch {
            val timestamp = Timestamp.now()
            val post = Post(
                userId = userId,
                content = content,
                imageUrl = imageUrl,
                timestamp = timestamp
            )
            db.collection("posts").add(post)
        }
    }

    fun getPosts() = db.collection("posts").orderBy("timestamp").get()
}