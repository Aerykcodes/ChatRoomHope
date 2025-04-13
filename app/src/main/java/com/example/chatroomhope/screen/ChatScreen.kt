package com.example.chatroomhope.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chatroomhope.ui.theme.ChatMessageItem
import com.example.chatroomhope.viewmodel.MessageViewModel
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    roomId: String,
    messageViewModel: MessageViewModel = viewModel(),
) {
    val messages by messageViewModel.messages.observeAsState(emptyList())
    val currentUserEmail = messageViewModel.currentUser.value?.email ?: ""
    val text = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    // Ensure roomId is set once when the screen is displayed
    LaunchedEffect(roomId) {
        messageViewModel.setRoomId(roomId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Display the chat messages
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(messages) { message ->
                ChatMessageItem(
                    message = message.copy(
                        isSentByCurrentUser = message.senderId == currentUserEmail
                    )
                )
            }
        }

        // Chat input field and send button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = text.value,
                onValueChange = { text.value = it },
                textStyle = TextStyle.Default.copy(fontSize = 16.sp),
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            )

            IconButton(
                onClick = {
                    if (text.value.isNotEmpty()) {
                        coroutineScope.launch {
                            messageViewModel.sendMessage(text.value.trim())
                            text.value = ""
                            messageViewModel.loadMessages()  // Refresh messages
                        }
                    }
                }
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = "Send")
            }
        }
    }
}
