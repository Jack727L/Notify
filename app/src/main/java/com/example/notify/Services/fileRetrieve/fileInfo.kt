package com.example.notify.Services.fileRetrieve

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class fileInfo {
    private val databaseReference = FirebaseDatabase.getInstance().reference.child("pdfs/MATH235")
    fun fetchLikesForDownloadUrl(downloadUrl: String, callback: (Int?) -> Unit) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children.forEach { childSnapshot ->
                    val url = childSnapshot.child("downloadUrl").getValue(String::class.java)
                    if (url == downloadUrl) {
                        val likes = childSnapshot.child("likes").getValue(Int::class.java)
                        callback(likes)
                        return
                    }
                }
                callback(null)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("FirebaseService", "fetchLikesForDownloadUrl:onCancelled", databaseError.toException())
                callback(null)
            }
        })
    }

    fun updateLikesForDownloadUrl(downloadUrl: String) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children.forEach { childSnapshot ->
                    val url = childSnapshot.child("downloadUrl").getValue(String::class.java)
                    if (url == downloadUrl) {
                        val currentLikes = childSnapshot.child("likes").getValue(Int::class.java) ?: 0
                        val newLikes = currentLikes + 1
                        childSnapshot.ref.child("likes").setValue(newLikes)
                        return
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("FirebaseService", "updateLikesForDownloadUrl:onCancelled", databaseError.toException())
            }
        })
    }
}
