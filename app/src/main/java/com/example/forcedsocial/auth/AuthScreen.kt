package com.example.forcedsocial.auth


import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult


@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    navController: NavController
) {
    val context = LocalContext.current

    val signInLauncher = rememberLauncherForActivityResult(
        contract = FirebaseAuthUIActivityResultContract()
    ) { result: FirebaseAuthUIAuthenticationResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            navController.navigate("home")
        }
    }

    if (viewModel.currentUser == null) {
        // Display the Firebase Auth UI
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        LaunchedEffect(Unit) {
            signInLauncher.launch(signInIntent)
        }
    } else {
        // User is signed in
        navController.navigate("home")
    }
}