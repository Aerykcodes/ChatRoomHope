package com.example.chatroomhope.viewmodel

import com.example.chatroomhope.data.injection
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatroomhope.data.UserRepository


import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel: ViewModel() {
    private val userRepository: UserRepository

    init {
        userRepository = UserRepository(
            FirebaseAuth.getInstance(),
            injection.instance()
        )
    }

    private val _authResult = MutableLiveData<com.example.chatroomhope.data.Result<Boolean>>()
    val authResult: LiveData<com.example.chatroomhope.data.Result<Boolean>> get() = _authResult

    // Toast message StateFlow
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // Function to show toast messages
    fun showToast(message: String) {
        _toastMessage.value = message
    }

    // Function to clear toast after showing
    fun clearToast() {
        _toastMessage.value = null
    }

    fun signUp(email: String, password: String, firstName: String, lastName: String) {
        viewModelScope.launch {
            _authResult.value = userRepository.signUp(email, password, firstName, lastName)
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authResult.value = userRepository.login(email, password)
        }
    }
}