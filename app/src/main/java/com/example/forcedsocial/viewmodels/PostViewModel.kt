package com.example.forcedsocial.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forcedsocial.models.Post
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch

class PostViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private var lastVisiblePost: DocumentSnapshot? = null
    private var lastVisiblePostByTopic: MutableMap<String, DocumentSnapshot?> = mutableMapOf()

    fun createPost(userId: String, content: String, imageUrl: String?, topicId: String) {
        if (content.isEmpty()) {
            return
        }
        viewModelScope.launch {
            val timestamp = com.google.firebase.Timestamp.now()
            val post = Post(
                userId = userId,
                content = content,
                imageUrl = imageUrl,
                timestamp = timestamp,
                topicId = topicId
            )
            withContext(Dispatchers.IO) {
                db.collection("posts").add(post).await()
            }
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                db.collection("posts").document(postId).delete().await()
            }
        }
    }

    suspend fun getPosts(limit: Long = 20): List<Post> {
        return withContext(Dispatchers.IO) {
            try {
                val querySnapshot = db.collection("posts")
                    .orderBy("timestamp")
                    .startAfter(lastVisiblePost!!)
                    .limit(limit)
                    .get()
                    .await()
                if (querySnapshot.size() > 0) {
                    lastVisiblePost = querySnapshot.documents[querySnapshot.size() - 1]
                }
                querySnapshot.toObjects(Post::class.java)
            } catch (e: Exception) {
                // Handle exception
                emptyList()
            }
        }
    }

    suspend fun getPostsByTopicId(topicId: String, limit: Long = 20): List<Post> {
        return withContext(Dispatchers.IO) {
            try {
                val query = if (lastVisiblePostByTopic[topicId] == null) {
                    db.collection("posts")
                        .whereEqualTo("topicId", topicId)
                        .orderBy("timestamp")
                        .limit(limit)
                } else {
                    db.collection("posts")
                        .whereEqualTo("topicId", topicId)
                        .orderBy("timestamp")
                        .startAfter(lastVisiblePostByTopic[topicId]!!)
                        .limit(limit)
                }

                val querySnapshot = query.get().await()
                if (querySnapshot.size() > 0) {
                    lastVisiblePostByTopic[topicId] =
                        querySnapshot.documents[querySnapshot.size() - 1]
                }
                querySnapshot.toObjects(Post::class.java)
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
}
