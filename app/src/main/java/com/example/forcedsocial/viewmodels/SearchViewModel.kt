package com.example.forcedsocial.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forcedsocial.models.Post
import com.example.forcedsocial.models.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _searchPostResults = MutableStateFlow<List<Post>>(emptyList())
    private val _searchUserResults = MutableStateFlow<List<User>>(emptyList())
    val searchPostResults: StateFlow<List<Post>> = _searchPostResults
    val searchUserResults: StateFlow<List<User>> = _searchUserResults

    private val _noPostResultsFound = MutableStateFlow(false)
    val noPostResultsFound: StateFlow<Boolean> = _noPostResultsFound

    private val _noUserResultsFound = MutableStateFlow(false)
    val noUserResultsFound: StateFlow<Boolean> = _noUserResultsFound

    fun searchPosts(query: String) {
        db.collection("posts")
            .get()
            .addOnSuccessListener { result ->
                val posts = result.map { document -> document.toObject(Post::class.java) }
                val filteredPosts = posts.filter { it.content.contains(query, ignoreCase = true) }
                viewModelScope.launch {
                    _searchPostResults.emit(filteredPosts)
                    _noPostResultsFound.emit(filteredPosts.isEmpty())
                }
            }
            .addOnFailureListener {
                // Handle error
            }

        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                val users = result.map { document -> document.toObject(User::class.java) }
                val filteredUsers = users.filter { user -> user.displayName.contains(query, ignoreCase = true) || user.username.contains(query, ignoreCase = true) }
                viewModelScope.launch {
                    _searchUserResults.emit(filteredUsers)
                    _noUserResultsFound.emit(filteredUsers.isEmpty())
                }
            }
            .addOnFailureListener {
                // Handle error
            }
    }
}