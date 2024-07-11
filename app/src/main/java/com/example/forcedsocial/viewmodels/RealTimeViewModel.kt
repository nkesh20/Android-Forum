package com.example.forcedsocial.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forcedsocial.models.Post
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RealTimeViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    fun getRealTimePosts(onUpdate: (List<Post>) -> Unit): ListenerRegistration {
        return db.collection("posts")
            .orderBy("timestamp")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    // Handle the error
                    println("Listen failed: $e")
                    return@addSnapshotListener
                }

                snapshots?.let { snapshot ->
                    val posts = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Post::class.java)?.apply {
                            // Convert Firestore Timestamp to Post timestamp
                            timestamp = doc.getTimestamp("timestamp")
                        }
                    }
                    onUpdate(posts)
                }
            }
    }
}