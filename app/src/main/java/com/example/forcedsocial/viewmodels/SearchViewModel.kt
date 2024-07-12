package com.example.forcedsocial.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forcedsocial.models.Post
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _searchResults = MutableStateFlow<List<Post>>(emptyList())
    val searchResults: StateFlow<List<Post>> = _searchResults

    private val _noResultsFound = MutableStateFlow(false)
    val noResultsFound: StateFlow<Boolean> = _noResultsFound

    fun searchPosts(query: String) {
        db.collection("posts")
            .get()
            .addOnSuccessListener { result ->
                val posts = result.map { document -> document.toObject(Post::class.java) }
                val filteredPosts = posts.filter { it.content.contains(query, ignoreCase = true) }
                viewModelScope.launch {
                    _searchResults.emit(filteredPosts)
                    _noResultsFound.emit(filteredPosts.isEmpty())
                }
            }
            .addOnFailureListener {
                // Handle error
            }
    }
}