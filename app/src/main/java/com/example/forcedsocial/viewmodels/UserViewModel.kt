package com.example.forcedsocial.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forcedsocial.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch
import java.util.UUID

class UserViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun createUserProfile(username: String, displayName: String, profilePictureUri: Uri?) {
        val userId = auth.currentUser?.uid ?: return
        val storage = Firebase.storage
        val storageRef = storage.reference

        val imageName = UUID.randomUUID()

        val spaceRef: StorageReference = storageRef.child("images/${imageName}.jpg")

        val uploadTask = profilePictureUri?.let { spaceRef.putFile(it) }

        uploadTask?.addOnFailureListener {
            Log.i("Image upload", "Failed")
            val user = User(userId, username, displayName, null)

            viewModelScope.launch {
                db.collection("users").document(userId).set(user)
            }
        }?.addOnSuccessListener {
            Log.i("Image upload", "Success")
            spaceRef.downloadUrl.addOnSuccessListener {
                val imageDownloadUrl = it.toString()
                Log.i("Image url", imageDownloadUrl)
                val user = User(userId, username, displayName, imageDownloadUrl)

                viewModelScope.launch {
                    db.collection("users").document(userId).set(user)
                }
            }.addOnFailureListener {
                val user = User(userId, username, displayName, null)

                viewModelScope.launch {
                    db.collection("users").document(userId).set(user)
                }
            }
        }
    }

    fun getUserProfile(userId: String) = db.collection("users").document(userId).get()
}