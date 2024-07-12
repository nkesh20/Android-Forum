package com.example.forcedsocial.screens

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.forcedsocial.auth.AuthViewModel
import com.example.forcedsocial.components.UserProfile
import com.example.forcedsocial.models.User
import com.example.forcedsocial.viewmodels.UserViewModel

@Composable
fun UserProfileScreen(userId: String, navController: NavController, authViewModel: AuthViewModel) {
    val context = LocalContext.current
    val userViewModel: UserViewModel = viewModel()
    var user by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            userViewModel.getUserProfile(userId).addOnSuccessListener {
                user = it.toObject(User::class.java)
            }.addOnFailureListener {
                Toast.makeText(
                    context,
                    "Couldn't get the User",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    BottomNavigationLayout(navController = navController, authViewModel = authViewModel) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (user != null) {
                UserProfile(username = user?.username?: "", displayName = user?.displayName?: "", profilePicture = if (!user?.profilePictureUrl.isNullOrEmpty()) Uri.parse(user?.profilePictureUrl) else null)
            }
        }
    }
}