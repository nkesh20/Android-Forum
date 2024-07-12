package com.example.forcedsocial.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class User(
    @DocumentId
    val id: String = "",
    val username: String = "",
    val displayName: String = "",
    val profilePictureUrl: String? = null,
    @get:PropertyName("accountType") @set:PropertyName("accountType") var accountType: AccountType = AccountType.USER
) {
    enum class AccountType(val type: String) {
        @PropertyName("user")
        USER("user"),

        @PropertyName("moderator")
        MODERATOR("moderator")
    }
}