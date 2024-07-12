package com.example.forcedsocial.auth


import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.forcedsocial.R
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    val signInLauncher = rememberLauncherForActivityResult(
        contract = FirebaseAuthUIActivityResultContract()
    ) { result: FirebaseAuthUIAuthenticationResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val user = auth.currentUser
            user?.let {
                db.collection("users").document(it.uid).get().addOnSuccessListener { document ->
                    if (!document.exists()) {
                        val newUser = mapOf(
                            "id" to it.uid,
                            "username" to it.email,
                            "profilePictureUrl" to null
                        )
                        db.collection("users").document(it.uid).set(newUser)
                    }
                    navController.navigate("topics")
                }
            }
        }
    }

    if (viewModel.currentUser == null) {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setIsSmartLockEnabled(false) // Disable automatic sign-in
            .setTheme(R.style.LoginTheme)
            .build()
        LaunchedEffect(Unit) {
            signInLauncher.launch(signInIntent)
        }
    } else {
        navController.navigate("topics")
    }
}