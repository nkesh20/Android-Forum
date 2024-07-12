package com.example.forcedsocial.screens

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.forcedsocial.auth.AuthViewModel
import com.example.forcedsocial.components.InputTextField
import com.example.forcedsocial.components.ProfileImageUpload
import com.example.forcedsocial.models.User
import com.example.forcedsocial.viewmodels.UserViewModel
import com.google.firebase.auth.FirebaseAuth

private fun getImageUri(imageUriString: String?): Uri? {
    if (imageUriString == null)
        return null

    return Uri.parse(imageUriString)
}

@Composable
fun ProfileScreen(authViewModel: AuthViewModel, navController: NavController) {
    val userViewModel: UserViewModel = viewModel()
    val user = FirebaseAuth.getInstance().currentUser
    val userData = remember { mutableStateOf<User?>(null) }
    val imageUri = remember { mutableStateOf(user?.photoUrl) }

    LaunchedEffect(Unit) {
        if (user != null) {
            userViewModel.getUserProfile(user.uid).addOnSuccessListener {
                userData.value = it.toObject((User::class.java))
                imageUri.value = getImageUri(userData.value?.profilePictureUrl)
            }
        }
    }

    var username = userData.value?.username ?: ""
    var displayName = userData.value?.displayName ?: ""

    val context = LocalContext.current


    Log.i("USER_INFO", "${userData.value?.username}")
    Log.i("USER_INFO", "${userData.value?.displayName}")

    BottomNavigationLayout(navController, authViewModel) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    ProfileImageUpload(
                        initialImage = imageUri.value,
                        onUpload = {
                            imageUri.value = it
                        },
                    )

                    InputTextField(
                        label = "Username",
                        prefill = userData.value?.username ?: "",
                        onTextChange = { username = it }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    InputTextField(
                        label = "Display Name",
                        prefill = userData.value?.displayName ?: "",
                        onTextChange = { displayName = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            userViewModel.createUserProfile(
                                username,
                                displayName,
                                imageUri.value,
                                getImageUri(userData.value?.profilePictureUrl),
                                userData.value?.accountType ?: User.AccountType.USER,
                                context,
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Save Profile")
                    }
                }
            }
        }
    }
}