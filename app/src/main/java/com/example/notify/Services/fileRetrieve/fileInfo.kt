package com.example.notify.Services.fileRetrieve

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener

class fileInfo {
    private val databaseReference = FirebaseDatabase.getInstance().reference.child("pdfs/MATH235")
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

    fun incrementLikesBasedOnKey(pushKey: String) {
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
                    Log.d("FirebaseService", "Likes successfully updated for $pushKey")
                }
            }
        })
    }
    fun decrementLikesBasedOnKey(pushKey: String) {
        val specificReference = databaseReference.child(pushKey)
        specificReference.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val currentLikes = mutableData.child("likes").getValue(Int::class.java) ?: 0
                mutableData.child("likes").value = currentLikes - 1
                return Transaction.success(mutableData)
            }
            override fun onComplete(databaseError: DatabaseError?, committed: Boolean, dataSnapshot: DataSnapshot?) {
                if (databaseError != null) {
                    Log.w("FirebaseService", "updateLikesForPushKey:onComplete:error", databaseError.toException())
                } else if (committed) {
                    Log.d("FirebaseService", "Likes successfully updated for $pushKey")
                }
            }
        })
    }
}
