package com.example.chatroomhope.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class MessageRepository(private val firestore: FirebaseFirestore) {

    suspend fun sendMessage(roomId: String, message: Message): Result<Unit> = try {
        firestore.collection("rooms").document(roomId)
            .collection("messages").add(message).await()
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    fun getChatMessages(roomId: String): Flow<List<Message>> = callbackFlow {
        val subscription = firestore.collection("rooms").document(roomId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { querySnapshot, error ->
                if (error != null) {
                    // Handle error here
                    return@addSnapshotListener
                }

                querySnapshot?.let { snapshot ->
                    val messages = snapshot.documents.mapNotNull { doc ->
                        try {
                            // Use the custom converter instead of standard toObject
                            Message.fromDocument(doc)
                        } catch (e: Exception) {
                            null // Skip documents that can't be parsed
                        }
                    }
                    trySend(messages).isSuccess
                }
            }

        awaitClose { subscription.remove() }
    }
}