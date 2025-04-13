package com.example.chatroomhope.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.chatroomhope.viewmodel.AuthViewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavController
import com.example.chatroomhope.data.Result

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    navController: NavController, // ✅ Do NOT reference this in default params
    onNavigateToSignUp: () -> Unit = {} // ✅ Provide an empty default, then override inside
) {
    val actualOnNavigateToSignUp = onNavigateToSignUp.takeIf { it != {} }
        ?: { navController.navigate(Screen.SignupScreen.route) } // ✅ Now we can safely use navController

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val authResult by authViewModel.authResult.observeAsState()

    LaunchedEffect(authResult) {
        authResult?.let { result ->
            if (result is Result.Success && result.data) { // ✅ Explicitly check type
                navController.navigate(Screen.ChatRoomsScreen.route) { // ✅ Use correct reference
                    popUpTo(Screen.LoginScreen.route) { inclusive = true }
                }

            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Log In",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val trimmedEmail = email.trim()
                if (trimmedEmail.isEmpty() || password.isEmpty()) {
                    authViewModel.showToast("Email and password are required!")
                    return@Button
                }
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
                    authViewModel.showToast("Invalid email format!")
                    return@Button
                }
                authViewModel.login(trimmedEmail, password)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Log In")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = actualOnNavigateToSignUp) { // ✅ Use the fixed variable
            Text("Don't have an account? Sign Up")
        }
    }
}
