package com.example.chatroomhope.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatroomhope.data.injection
import com.example.chatroomhope.data.Result
import com.example.chatroomhope.data.Room
import com.example.chatroomhope.data.RoomRepository

import kotlinx.coroutines.launch

class RoomViewModel : ViewModel() {

    private val _rooms = MutableLiveData<List<Room>>()
    val rooms: LiveData<List<Room>> get() = _rooms
    private val roomRepository: RoomRepository
    init {
        roomRepository = RoomRepository(injection.instance())
        loadRooms()
    }

    fun createRoom(name: String) {
        viewModelScope.launch {
            roomRepository.createRoom(name)
        }
    }

    fun loadRooms() {
        viewModelScope.launch {
            when (val result = roomRepository.getRooms()) {
                is Result.Success -> _rooms.value = result.data
                is Result.Error -> {
                    // Handle error case here, e.g.:
                    Log.e("loadRooms", "Error fetching rooms", result.exception)
                    // optionally show a toast or update UI state
                }
            }
        }
    }


}