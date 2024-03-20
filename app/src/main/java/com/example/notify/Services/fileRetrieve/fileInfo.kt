package com.example.notify.Services.fileRetrieve

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener

class fileInfo {
    private val databaseReference = FirebaseDatabase.getInstance().reference.child("pdfs")
    private val userDatabaseReference = FirebaseDatabase.getInstance().reference.child("users")
    // use our pushKey as the child to find corresponding number of likes
    fun fetchLikesForPushKey(pushKey: String, callback: (Int?) -> Unit) {
        val specificReference = databaseReference.child(pushKey)
        specificReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val likes = dataSnapshot.child("likes").getValue(Int::class.java)
                callback(likes)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("FirebaseService", "fetchLikesForPushKey:onCancelled", databaseError.toException())
                callback(null)
            }
        })
    }
    // update likes based on if we want to increment it or decrement it
    fun updateLikesBasedOnPushkey(pushKey: String, userId: String, increment: Boolean) {
        val userLikeDatabaseReference = userDatabaseReference.child(userId).child("user_likes").child(pushKey)
        if (increment) {
            // Increment like
            userLikeDatabaseReference.setValue(true).addOnSuccessListener {
                Log.d("FirebaseService", "Post liked by user $userId with pushKey $pushKey")
            }.addOnFailureListener { exception ->
                Log.w("FirebaseService", "Error setting like for user $userId with pushKey $pushKey", exception)
            }
            val specificReference = databaseReference.child(pushKey)
            specificReference.runTransaction(object : Transaction.Handler {
                override fun doTransaction(mutableData: MutableData): Transaction.Result {
                    val currentLikes = mutableData.child("likes").getValue(Int::class.java) ?: 0
                    mutableData.child("likes").value = currentLikes + 1
                    return Transaction.success(mutableData)
                }
                override fun onComplete(databaseError: DatabaseError?, committed: Boolean, dataSnapshot: DataSnapshot?) {
                    if (databaseError != null) {
                        Log.w("FirebaseService", "updateLikesForPushKey:onComplete:error", databaseError.toException())
                    } else if (committed) {
                        Log.d("FirebaseService", "Likes successfully incremented for $pushKey")
                    }
                }
            })
        } else {
            // Decrement like
            userLikeDatabaseReference.removeValue().addOnSuccessListener {
                Log.d("FirebaseService", "Post unliked by user $userId with pushKey $pushKey")
            }.addOnFailureListener { exception ->
                Log.w("FirebaseService", "Error removing like for user $userId with pushKey $pushKey", exception)
            }
            val specificReference = databaseReference.child(pushKey)
            specificReference.runTransaction(object : Transaction.Handler {
                override fun doTransaction(mutableData: MutableData): Transaction.Result {
                    val currentLikes = mutableData.child("likes").getValue(Int::class.java) ?: 0
                    mutableData.child("likes").value = (currentLikes - 1).coerceAtLeast(0) // Prevent negative likes
                    return Transaction.success(mutableData)
                }
                override fun onComplete(databaseError: DatabaseError?, committed: Boolean, dataSnapshot: DataSnapshot?) {
                    if (databaseError != null) {
                        Log.w("FirebaseService", "updateLikesForPushKey:onComplete:error", databaseError.toException())
                    } else if (committed) {
                        Log.d("FirebaseService", "Likes successfully decremented for $pushKey")
                    }
                }
            })
        }
    }
    // update collects based on boolean and pushKey
    fun updateCollectsBasedOnPushkey(pushKey: String, userId: String, collects: Boolean) {
        val userLikeDatabaseReference = userDatabaseReference.child(userId).child("user_collects").child(pushKey)
        if (collects) {
            // Increment like
            userLikeDatabaseReference.setValue(true).addOnSuccessListener {
                Log.d("FirebaseService", "Post liked by user $userId with pushKey $pushKey")
            }.addOnFailureListener { exception ->
                Log.w("FirebaseService", "Error setting like for user $userId with pushKey $pushKey", exception)
            }
        } else {
            // Decrement like
            userLikeDatabaseReference.removeValue().addOnSuccessListener {
                Log.d("FirebaseService", "Post unliked by user $userId with pushKey $pushKey")
            }.addOnFailureListener { exception ->
                Log.w("FirebaseService", "Error removing like for user $userId with pushKey $pushKey", exception)
            }
        }
    }

}
