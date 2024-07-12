package com.example.forcedsocial.viewmodels

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forcedsocial.models.Comment
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

class CommentViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    fun createComment(
        userId: String,
        postId: String,
        content: String,
        imageUri: Uri?,
        context: Context
    ) {
        if (content.isEmpty()) {
            return
        }

        if (imageUri == null) {
            viewModelScope.launch {
                try {
                    val timestamp = Timestamp.now()
                    val comment = Comment(
                        userId = userId,
                        postId = postId,
                        content = content,
                        imageUrl = null,
                        timestamp = timestamp
                    )
                    withContext(Dispatchers.IO) {
                        db.collection("comments").add(comment).await()
                    }
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Comment added successfully!", Toast.LENGTH_SHORT)
                            .show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Comment creation failed, please try again",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            return
        }

        val storage = Firebase.storage
        val storageRef = storage.reference

        val imageName = UUID.randomUUID()

        val spaceRef = storageRef.child("images/${imageName}.jpg")

        val uploadTask = imageUri.let { spaceRef.putFile(it) }

        uploadTask.addOnFailureListener {
            Toast.makeText(context, "Comment creation failed, please try again", Toast.LENGTH_SHORT)
                .show()
        }.addOnSuccessListener {
            spaceRef.downloadUrl.addOnSuccessListener { uri ->
                viewModelScope.launch {
                    try {
                        val timestamp = Timestamp.now()
                        val comment = Comment(
                            userId = userId,
                            postId = postId,
                            content = content,
                            imageUrl = uri.toString(),
                            timestamp = timestamp
                        )
                        withContext(Dispatchers.IO) {
                            db.collection("comments").add(comment).await()
                        }
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                "Comment added successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                "Comment creation failed, please try again",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(
                    context,
                    "Comment creation failed, please try again",
                    Toast.LENGTH_SHORT
                ).show()
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

    fun deletePostComments(postId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val querySnapshot = db.collection("comments")
                        .whereEqualTo("postId", postId)
                        .get()
                        .await()

                    for (document in querySnapshot.documents) {
                        document.reference.delete().await()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
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
