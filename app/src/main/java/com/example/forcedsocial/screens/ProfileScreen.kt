package com.example.forcedsocial.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.forcedsocial.auth.AuthViewModel
import com.example.forcedsocial.viewmodels.UserProfileViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(authViewModel: AuthViewModel, navController: NavController) {
    val userProfileViewModel: UserProfileViewModel = viewModel()
    val user = FirebaseAuth.getInstance().currentUser
    val username = remember { mutableStateOf(user?.email ?: "") }
    val displayName = remember { mutableStateOf(user?.displayName ?: "") }


    BottomNavigationLayout(navController, authViewModel) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                BasicTextField(
                    value = username.value,
                    onValueChange = { username.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                BasicTextField(
                    value = displayName.value,
                    onValueChange = { displayName.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    userProfileViewModel.createUserProfile(username.value, displayName.value, null)
                }) {
                    Text(text = "Save Profile")
                }
            }
        }
    }
}