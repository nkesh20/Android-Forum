package com.example.forcedsocial.viewmodels

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forcedsocial.models.Post
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import java.util.UUID

class PostViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private var lastVisiblePost: DocumentSnapshot? = null
    private var lastVisiblePostByTopic: MutableMap<String, DocumentSnapshot?> = mutableMapOf()

    private val _postCreationSuccess = MutableLiveData<Boolean>()
    val postCreationSuccess: LiveData<Boolean> = _postCreationSuccess

    fun createPost(
        userId: String,
        content: String,
        imageUri: Uri?,
        topicId: String,
        context: Context
    ) {
        if (content.isEmpty()) {
            return
        }

        if (imageUri == null) {
            viewModelScope.launch {
                try {
                    val timestamp = com.google.firebase.Timestamp.now()
                    val post = Post(
                        userId = userId,
                        content = content,
                        imageUrl = null,
                        timestamp = timestamp,
                        topicId = topicId
                    )
                    withContext(Dispatchers.IO) {
                        db.collection("posts").add(post).await()
                    }
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Successfully posted!", Toast.LENGTH_SHORT).show()
                        _postCreationSuccess.value = true
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Post creation failed, please try again",
                            Toast.LENGTH_SHORT
                        ).show()
                        _postCreationSuccess.value = false
                    }
                }
            }
            return
        }

        val storage = Firebase.storage
        val storageRef = storage.reference

        val imageName = UUID.randomUUID()

        val spaceRef: StorageReference = storageRef.child("images/${imageName}.jpg")

        val uploadTask = imageUri.let { spaceRef.putFile(it) }

        uploadTask.addOnFailureListener {
            Toast.makeText(context, "Post creation failed, please try again", Toast.LENGTH_SHORT)
                .show()
            _postCreationSuccess.value = false
        }.addOnSuccessListener {
            spaceRef.downloadUrl.addOnSuccessListener {
                val imageUrl = it.toString()
                viewModelScope.launch {
                    try {
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
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Successfully posted!", Toast.LENGTH_SHORT)
                                .show()
                            _postCreationSuccess.value = true
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                "Post creation failed, please try again",
                                Toast.LENGTH_SHORT
                            ).show()
                            _postCreationSuccess.value = false
                        }
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(
                    context,
                    "Post creation failed, please try again",
                    Toast.LENGTH_SHORT
                ).show()
                _postCreationSuccess.value = false
            }
        }
    }

    fun getPostById(postId: String) = db.collection("posts").document(postId).get()

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
