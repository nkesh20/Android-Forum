package com.example.forcedsocial

import AuthRepository
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.forcedsocial.auth.AuthScreen
import com.example.forcedsocial.auth.AuthViewModel
import com.example.forcedsocial.auth.AuthViewModelFactory
import com.example.forcedsocial.screens.CreatePostScreen
import com.example.forcedsocial.screens.PostDiscussion
import com.example.forcedsocial.screens.ProfileScreen
import com.example.forcedsocial.screens.PostListScreen
import com.example.forcedsocial.screens.SearchScreen
import com.example.forcedsocial.screens.TopicsScreen
import com.example.forcedsocial.ui.theme.ForcedSocialTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(AuthRepository(FirebaseAuth.getInstance()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ForcedSocialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(authViewModel)
                }
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: AuthViewModel) {
    val navController = rememberNavController()

    val currentUser by viewModel.currentUser.observeAsState()

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            navController.navigate("topics") {
                popUpTo("home") { inclusive = true }
            }
        } else {
            navController.navigate("home") {
                popUpTo("topics") { inclusive = true }
            }
        }
    }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("auth") { AuthScreen(viewModel, navController) }
        composable("topics") { TopicsScreen(viewModel, navController) }
        composable(
            "posts/{topicId}",
            arguments = listOf(navArgument("topicId") { type = NavType.StringType })
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId") ?: ""
            PostListScreen(
                navController = navController,
                authViewModel = viewModel,
                topicId = topicId
            )
        }
        composable("search") { SearchScreen(viewModel, navController) }
        composable("profile") { ProfileScreen(viewModel, navController) }
        composable(
            "createPost/{topicId}",
            arguments = listOf(navArgument("topicId") { type = NavType.StringType })
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId") ?: ""
            CreatePostScreen(navController, viewModel, topicId)
        }
        composable(route = "postDiscussion?postId={postId}", listOf(
            navArgument(name = "postId") {
                type = NavType.StringType
                defaultValue = ""
            }
        )) { backStackEntry ->
            PostDiscussion()
        }
    }
}

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to ForcedSocial",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        Button(
            onClick = { navController.navigate("auth") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text(text = "Sign in")
        }
        Button(
            onClick = { navController.navigate("topics") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Continue without signing in")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    ForcedSocialTheme {
        HomeScreen(rememberNavController())
    }
}