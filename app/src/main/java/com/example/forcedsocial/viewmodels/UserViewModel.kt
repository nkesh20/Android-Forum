package com.example.forcedsocial.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forcedsocial.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun createUserProfile(username: String, displayName: String, profilePictureUrl: String?) {
        val userId = auth.currentUser?.uid ?: return
        val user = User(userId, username, displayName, profilePictureUrl)

        viewModelScope.launch {
            db.collection("users").document(userId).set(user)
        }
    }

    fun getUserProfile(userId: String) = db.collection("users").document(userId).get()
}