package com.example.forcedsocial.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forcedsocial.models.Notification
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class NotificationViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    fun createNotification(
        senderId: String,
        recipientId: String,
        postId: String,
        notificationType: Notification.NotificationType
    ) {
        viewModelScope.launch {
            try {
                val timestamp = Timestamp.now()
                val notification = Notification(
                    senderId = senderId,
                    recipientId = recipientId,
                    postId = postId,
                    notificationType = notificationType,
                    timestamp = timestamp
                )
                db.collection("notifications").add(notification).await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteNotification(notificationId: String) {
        viewModelScope.launch {
            try {
                db.collection("notifications").document(notificationId).delete().await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getRealTimeNotifications(
        recipientId: String,
        onUpdate: (List<Notification>) -> Unit
    ): ListenerRegistration {
        val query = db.collection("notifications")
            .whereEqualTo("recipientId", recipientId)
            .orderBy("timestamp", Query.Direction.DESCENDING)

        return query.addSnapshotListener { snapshots, e ->
            if (e != null) {
                println("Listen failed: $e")
                return@addSnapshotListener
            }

            snapshots?.let { snapshot ->
                val notifications = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Notification::class.java)?.apply {
                        // Convert Firestore Timestamp to Notification timestamp
                        timestamp = doc.getTimestamp("timestamp")
                    }
                }
                onUpdate(notifications)
            }
        }
    }
}
