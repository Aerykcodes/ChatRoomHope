package com.example.chatroomhope.screen

sealed class Screen(val route: String) {
    object LoginScreen : Screen("loginscreen")
    object SignupScreen : Screen("signupscreen")
    object ChatRoomsScreen : Screen("chatroomscreen")
    object ChatRoomListScreen : Screen("chatroomlistscreen")
    object ChatScreen: Screen("ChatScreen")
}
