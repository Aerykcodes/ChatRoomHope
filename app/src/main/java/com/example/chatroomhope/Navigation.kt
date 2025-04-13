package com.example.chatroomhope

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.chatroomhope.screen.ChatRoomListScreen

import com.example.chatroomhope.screen.LoginScreen
import com.example.chatroomhope.screen.Screen
import com.example.chatroomhope.screen.SignUpScreen
import com.example.chatroomhope.viewmodel.AuthViewModel
import com.example.chatroomhope.screen.ChatScreen

@Composable
fun NavigationGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current

    val toastMessage by authViewModel.toastMessage.collectAsState()

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            authViewModel.clearToast()
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.SignupScreen.route // Correct route reference!
    ) {
        composable(Screen.SignupScreen.route) {
            SignUpScreen(
                authViewModel,
                onNavigateToLogin = { navController.navigate(Screen.LoginScreen.route) }
            )
        }
        composable(Screen.LoginScreen.route) {
            LoginScreen(
                authViewModel,
                navController = navController,
                onNavigateToSignUp = { navController.navigate(Screen.SignupScreen.route) }
            )
        }
        composable(Screen.ChatRoomsScreen.route) {
            ChatRoomListScreen { roomId ->
                navController.navigate("${Screen.ChatScreen.route}/${roomId}")
            }
        }

        composable("${Screen.ChatScreen.route}/{roomId}") {
            val roomId: String = it
                .arguments?.getString("roomId") ?: ""
            ChatScreen(roomId = roomId)
        }
    }
}
