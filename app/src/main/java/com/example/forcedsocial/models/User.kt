package com.example.forcedsocial.models

import com.google.firebase.firestore.DocumentId

data class UserProfile(
    @DocumentId val id: String = "",
    val username: String = "",
    val profilePictureUrl: String? = null
)