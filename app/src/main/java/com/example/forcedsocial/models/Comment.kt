package com.example.forcedsocial.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Comment(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val postId: String,
    val content: String,
    val imageUrl: String? = null,
    var timestamp: Timestamp? = null
) {
    constructor() : this("", "", "", "", null, null)
}
