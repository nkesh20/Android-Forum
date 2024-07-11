package com.example.forcedsocial.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forcedsocial.models.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class UserProfileViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun createUserProfile(username: String, displayName: String, profilePictureUrl: String?) {
        val userId = auth.currentUser?.uid ?: return
        val userProfile = UserProfile(userId, username, profilePictureUrl)

        viewModelScope.launch {
            db.collection("users").document(userId).set(userProfile)
        }
    }

    fun getUserProfile(userId: String) = db.collection("users").document(userId).get()
}