package com.example.notify.Services.fileRetrieve

import android.util.Log
import com.example.notify.Services.UploadService.PdfFile
import com.example.notify.Services.UploadService.PdfFilesRetrievalCallback
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
    fun fetchColletsForPushKey(pushKey: String, callback: (Int?) -> Unit) {
        val specificReference = databaseReference.child(pushKey)
        specificReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val collects = dataSnapshot.child("collects").getValue(Int::class.java)
                callback(collects)
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
            val specificReference = databaseReference.child(pushKey)
            specificReference.runTransaction(object : Transaction.Handler {
                override fun doTransaction(mutableData: MutableData): Transaction.Result {
                    val currentCollects = mutableData.child("collects").getValue(Int::class.java) ?: 0
                    mutableData.child("collects").value = currentCollects + 1
                    return Transaction.success(mutableData)
                }
                override fun onComplete(databaseError: DatabaseError?, committed: Boolean, dataSnapshot: DataSnapshot?) {
                    if (databaseError != null) {
                        Log.w("FirebaseService", "updateCollectsForPushKey:onComplete:error", databaseError.toException())
                    } else if (committed) {
                        Log.d("FirebaseService", "Collects successfully incremented for $pushKey")
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
                    val currentCollects = mutableData.child("collects").getValue(Int::class.java) ?: 0
                    mutableData.child("collects").value = (currentCollects - 1).coerceAtLeast(0) // Prevent negative likes
                    return Transaction.success(mutableData)
                }
                override fun onComplete(databaseError: DatabaseError?, committed: Boolean, dataSnapshot: DataSnapshot?) {
                    if (databaseError != null) {
                        Log.w("FirebaseService", "updateCollectsForPushKey:onComplete:error", databaseError.toException())
                    } else if (committed) {
                        Log.d("FirebaseService", "Collects successfully decremented for $pushKey")
                    }
                }
            })
        }
    }
    // retrieve all collects files, the intake will be the current user id
    fun retrieveUserCollectedPdfFiles(userId: String, like_or_collect_or_post: String, callback: PdfFilesRetrievalCallback) {
        var userCollectsReference = userDatabaseReference.child(userId).child("user_collects")
        if (like_or_collect_or_post == "likes") {
            userCollectsReference = userDatabaseReference.child(userId).child("user_likes")
        } else if (like_or_collect_or_post == "posts") {
            userCollectsReference = userDatabaseReference.child(userId).child("user_posts")
        }
        // check for each pushKey for the user
        userCollectsReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(userSnapshot: DataSnapshot) {
                val pushKeys = userSnapshot.children.mapNotNull { it.key }
                if (pushKeys.isEmpty()) {
                    Log.e("retrieving", "User has no collects")
                    callback.onError("User has no collects")
                    return
                }
                val tempList = mutableListOf<PdfFile>()
                val databaseReference = FirebaseDatabase.getInstance().reference.child("pdfs")
                var completedRequests = 0
                // match that push key with the file under pdfs path
                pushKeys.forEach { pushKey ->
                    databaseReference.child(pushKey).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(pdfSnapshot: DataSnapshot) {
                            val pdfFile = pdfSnapshot.getValue(PdfFile::class.java)
                            pdfFile?.let {
                                tempList.add(it)
                            }
                            completedRequests++
                            if (completedRequests == pushKeys.size) {
                                if (tempList.isEmpty()) {
                                    Log.e("retrieving", "No matching PDF files found")
                                    callback.onError("No matching PDF files found")
                                } else {
                                    callback.onSuccess(tempList)
                                }
                            }
                        }
                        override fun onCancelled(pdfError: DatabaseError) {
                            completedRequests++
                            Log.e("retrieving", "Error retrieving PDF file for pushKey $pushKey", pdfError.toException())
                            if (completedRequests == pushKeys.size && tempList.isEmpty()) {
                                callback.onError("Error retrieving PDF files")
                            }
                        }
                    })
                }
            }
            override fun onCancelled(userError: DatabaseError) {
                Log.e("retrieving", "Error retrieving user collects", userError.toException())
                callback.onError(userError.toException().message ?: "Error retrieving user collects")
            }
        })
    }
}
