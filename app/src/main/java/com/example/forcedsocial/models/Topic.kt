package com.example.forcedsocial.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Topic(
    @DocumentId
    val id: String = "",
    val creatorId: String = "",
    val parentId: String? = null,
    val name: String = "",
    var timestamp: Timestamp? = null
)
