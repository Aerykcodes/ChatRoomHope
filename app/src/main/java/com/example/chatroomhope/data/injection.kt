package com.example.chatroomhope.data

import com.google.firebase.firestore.FirebaseFirestore

object injection {
    private val instance: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    fun instance(): FirebaseFirestore {
        return instance
    }
}









