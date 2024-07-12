package com.example.forcedsocial.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class Notification(
    @DocumentId
    val id: String = "",
    val senderId: String = "",
    val recipientId: String = "",
    val postId: String = "",
    @get:PropertyName("notificationType") @set:PropertyName("notificationType") var notificationType: NotificationType = NotificationType.COMMENT,
    val read: Boolean = false,
    var timestamp: Timestamp? = null
) {
    enum class NotificationType {
        @PropertyName("comment")
        COMMENT,

        @PropertyName("mention")
        MENTION
    }
}
