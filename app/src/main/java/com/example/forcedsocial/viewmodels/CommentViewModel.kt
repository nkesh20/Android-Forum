package com.example.forcedsocial.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forcedsocial.models.Comment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CommentViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    fun createComment(userId: String, postId: String, content: String, imageUrl: String?) {
        if (content.isEmpty()) {
            return
        }
        viewModelScope.launch {
            val timestamp = com.google.firebase.Timestamp.now()
            val comment = Comment(
                userId = userId,
                postId = postId,
                content = content,
                imageUrl = imageUrl,
                timestamp = timestamp
            )
            withContext(Dispatchers.IO) {
                db.collection("comments").add(comment).await()
            }
        }
    }

    fun deleteComment(commentId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                db.collection("comments").document(commentId).delete().await()
            }
        }
    }

    fun getRealTimeCommentsForPost(
        postId: String,
        onUpdate: (List<Comment>) -> Unit
    ): ListenerRegistration {
        val query = db.collection("comments")
            .whereEqualTo("postId", postId)
            .orderBy("timestamp")

        return query.addSnapshotListener { snapshots, e ->
            if (e != null) {
                println("Listen failed: $e")
                return@addSnapshotListener
            }

            snapshots?.let { snapshot ->
                val comments = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Comment::class.java)?.apply {
                        // Convert Firestore Timestamp to Notification timestamp
                        timestamp = doc.getTimestamp("timestamp")
                    }
                }
                onUpdate(comments)
            }
        }
    }
}
