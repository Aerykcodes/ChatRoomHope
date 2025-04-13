package com.example.chatroomhope.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Message(
    val senderFirstName: String = "",
    val senderId: String = "",
    val text: String = "",
    val isSentByCurrentUser: Boolean = false,
    @ServerTimestamp val timestamp: Timestamp? = null
) {
    // No-argument constructor for Firestore
    constructor() : this("", "", "", false, null)

    companion object {
        // Custom converter to handle Long timestamp values
        fun fromDocument(document: DocumentSnapshot): Message {
            // First try the standard conversion
            val message = document.toObject(Message::class.java)

            // If it fails due to timestamp issue, handle it manually
            if (message == null) {
                val data = document.data ?: return Message()

                // Get basic fields directly
                val senderFirstName = data["senderFirstName"] as? String ?: ""
                val senderId = data["senderId"] as? String ?: ""
                val text = data["text"] as? String ?: ""
                val isSentByCurrentUser = data["isSentByCurrentUser"] as? Boolean ?: false

                // Handle the timestamp conversion
                val timestamp = when (val timestampValue = data["timestamp"]) {
                    is Timestamp -> timestampValue
                    is Long -> Timestamp(Date(timestampValue))
                    is Date -> Timestamp(timestampValue)
                    else -> null
                }

                return Message(senderFirstName, senderId, text, isSentByCurrentUser, timestamp)
            }

            return message
        }
    }
}