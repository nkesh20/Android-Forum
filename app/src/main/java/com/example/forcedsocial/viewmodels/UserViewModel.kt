package com.example.forcedsocial.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forcedsocial.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.util.UUID

class UserViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()


    fun createUserProfile(
        username: String,
        displayName: String,
        profilePictureUri: Uri?,
        oldProfilePictureUri: Uri?,
        context: Context
    ) {
        val userId = auth.currentUser?.uid ?: return

        if (profilePictureUri == null || profilePictureUri.toString() == oldProfilePictureUri?.toString()) {
            val user = User(userId, username, displayName, profilePictureUri?.toString())

            viewModelScope.launch {
                db.collection("users").document(userId).set(user)
            }

            Toast.makeText(
                context,
                "Successfully updated user",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val storage = Firebase.storage
        val storageRef = storage.reference
        val imageName = UUID.randomUUID()
        val spaceRef: StorageReference = storageRef.child("images/${imageName}.jpg")
        val uploadTask = profilePictureUri.let { spaceRef.putFile(it) }

        uploadTask.addOnFailureListener {
            val user = User(userId, username, displayName, null)

            viewModelScope.launch {
                db.collection("users").document(userId).set(user)
            }
            Toast.makeText(
                context,
                "Failed to update user, please try again",
                Toast.LENGTH_SHORT
            ).show()
        }.addOnSuccessListener {
            spaceRef.downloadUrl.addOnSuccessListener {
                val imageDownloadUrl = it.toString()
                Log.i("Image url", imageDownloadUrl)
                val user = User(userId, username, displayName, imageDownloadUrl)

                viewModelScope.launch {
                    db.collection("users").document(userId).set(user)
                }
                Toast.makeText(
                    context,
                    "Successfully updated user",
                    Toast.LENGTH_SHORT
                ).show()
            }.addOnFailureListener {
                val user = User(userId, username, displayName, null)

                viewModelScope.launch {
                    db.collection("users").document(userId).set(user)
                }

                Toast.makeText(
                    context,
                    "Failed to get user picture",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun getUserProfile(userId: String) = db.collection("users").document(userId).get()

    fun canCreateTopic(userId: String?): Boolean {
        if (userId == null) return false

        return runBlocking {
            val userDoc = getUserProfile(userId).await()
            if (userDoc.exists()) {
                val accountType = userDoc.getString("accountType")
                accountType == User.AccountType.MODERATOR.type
            } else {
                false
            }
        }
    }
}