package com.example.forcedsocial.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forcedsocial.models.Post
import com.example.forcedsocial.models.Topic
import com.example.forcedsocial.models.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _searchPostResults = MutableStateFlow<List<Post>>(emptyList())
    private val _searchUserResults = MutableStateFlow<List<User>>(emptyList())
    private val _searchTopicResults = MutableStateFlow<List<Topic>>(emptyList())
    val searchPostResults: StateFlow<List<Post>> = _searchPostResults
    val searchUserResults: StateFlow<List<User>> = _searchUserResults
    val searchTopicResults: StateFlow<List<Topic>> = _searchTopicResults

    private val _noPostResultsFound = MutableStateFlow(false)
    val noPostResultsFound: StateFlow<Boolean> = _noPostResultsFound

    private val _noUserResultsFound = MutableStateFlow(false)
    val noUserResultsFound: StateFlow<Boolean> = _noUserResultsFound

    private val _noTopicResultsFound = MutableStateFlow(false)
    val noTopicResultsFound: StateFlow<Boolean> = _noTopicResultsFound

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
                val filteredUsers = users.filter { user ->
                    user.displayName.contains(
                        query,
                        ignoreCase = true
                    ) || user.username.contains(query, ignoreCase = true)
                }
                viewModelScope.launch {
                    _searchUserResults.emit(filteredUsers)
                    _noUserResultsFound.emit(filteredUsers.isEmpty())
                }
            }
            .addOnFailureListener {
                // Handle error
            }

        db.collection("topics")
            .get()
            .addOnSuccessListener { result ->
                val topics = result.map { document -> document.toObject(Topic::class.java) }
                val filteredTopics =
                    topics.filter { topic -> topic.name.contains(query, ignoreCase = true) }
                viewModelScope.launch {
                    _searchTopicResults.emit(filteredTopics)
                    _noTopicResultsFound.emit(filteredTopics.isEmpty())
                }
            }
            .addOnFailureListener {
                // Handle error
            }
    }
}