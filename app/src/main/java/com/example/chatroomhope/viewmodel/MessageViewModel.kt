package com.example.chatroomhope.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatroomhope.data.Message
import com.example.chatroomhope.data.MessageRepository
import com.example.chatroomhope.data.Result
import com.example.chatroomhope.data.User
import com.example.chatroomhope.data.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp  // 🔹 Import Firebase Timestamp
import kotlinx.coroutines.launch

class MessageViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()  // Initialize Firestore

    private val messageRepository = MessageRepository(firestore)  // Pass Firestore
    private val userRepository = UserRepository(FirebaseAuth.getInstance(), firestore)  // Pass Firestore

    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> get() = _messages

    private val _roomId = MutableLiveData<String?>()
    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> get() = _currentUser

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            when (val result = userRepository.getCurrentUser()) {
                is Result.Success -> _currentUser.value = result.data
                is Result.Error -> {
                    println("Error fetching user: ${result.exception.message}")
                }
            }
        }
    }

    fun loadMessages() {
        val roomId = _roomId.value.orEmpty()
        if (roomId.isNotEmpty()) {
            viewModelScope.launch {
                messageRepository.getChatMessages(roomId).collect {
                    _messages.value = it
                }
            }
        } else {
            println("Error: Room ID is empty. Cannot load messages.")
        }
    }

    fun sendMessage(text: String) {
        _currentUser.value?.let { user ->
            val message = Message(
                senderFirstName = user.firstName,
                senderId = user.email,
                text = text,
                timestamp = Timestamp.now(),  // 🔹 Firestore requires Timestamp, NOT Date
                isSentByCurrentUser = true
            )

            viewModelScope.launch {
                when (val result = messageRepository.sendMessage(_roomId.value.orEmpty(), message)) {
                    is Result.Success -> Unit
                    is Result.Error -> {
                        println("Error sending message: ${result.exception.message}")
                    }
                }
            }
        } ?: println("Error: Current user is null, cannot send message.")
    }

    fun setRoomId(roomId: String) {
        _roomId.value = roomId
        loadMessages()
    }
}
