package com.example.forcedsocial.auth


import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun AuthScreen(authViewModel: AuthViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var idToken by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            authViewModel.signInWithEmail(email, password,
                onSuccess = { /* Navigate to Home Screen */ },
                onError = { /* Show Error */ }
            )
        }) {
            Text("Sign In")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            authViewModel.signUpWithEmail(email, password,
                onSuccess = { /* Navigate to Home Screen */ },
                onError = { /* Show Error */ }
            )
        }) {
            Text("Sign Up")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            authViewModel.signInWithGoogle(idToken,
                onSuccess = { /* Navigate to Home Screen */ },
                onError = { /* Show Error */ }
            )
        }) {
            Text("Sign In with Google")
        }
    }
}