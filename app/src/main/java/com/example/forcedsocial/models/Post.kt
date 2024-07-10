package com.example.forcedsocial.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Post(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val content: String = "",
    val imageUrl: String? = null,
    var timestamp: Timestamp? = null
)