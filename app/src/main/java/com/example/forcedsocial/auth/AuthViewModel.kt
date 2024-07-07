package com.example.forcedsocial.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    fun signInWithEmail(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.signInWithEmail(email, password)
                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    fun signUpWithEmail(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.signUpWithEmail(email, password)
                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    fun signInWithGoogle(idToken: String, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                repository.signInWithGoogle(idToken)
                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    fun signOut() {
        repository.signOut()
    }

    fun getCurrentUser() = repository.getCurrentUser()
}