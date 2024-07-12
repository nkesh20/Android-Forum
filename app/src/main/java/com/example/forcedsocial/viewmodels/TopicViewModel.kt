package com.example.forcedsocial.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forcedsocial.models.Topic
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class TopicViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private var lastVisibleTopic: DocumentSnapshot? = null

    fun createTopic(userId: String, parentId: String, name: String) {
        viewModelScope.launch {
            try {
                val timestamp = Timestamp.now()
                val topic = Topic(
                    creatorId = userId,
                    parentId = parentId,
                    name = name,
                    timestamp = timestamp
                )
                db.collection("topics").add(topic).await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteTopic(topicId: String) {
        viewModelScope.launch {
            try {
                db.collection("topics").document(topicId).delete().await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun getAllTopics(): List<Topic> {
        return try {
            val query = if (lastVisibleTopic == null) {
                db.collection("topics")
                    .orderBy("timestamp")
            } else {
                db.collection("topics")
                    .orderBy("timestamp")
                    .startAfter(lastVisibleTopic!!)
            }

            val querySnapshot = query.get().await()
            if (!querySnapshot.isEmpty) {
                lastVisibleTopic = querySnapshot.documents[querySnapshot.size() - 1]
            }

            querySnapshot.toObjects(Topic::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
