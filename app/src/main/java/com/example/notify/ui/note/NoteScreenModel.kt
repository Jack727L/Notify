package com.example.notify.ui.note

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.notify.Services.fileRetrieve.fileInfo
import java.util.concurrent.ArrayBlockingQueue

class NoteScreenModel : ViewModel()  {
    private val infoRetrieve = fileInfo()
    private var like = 0

    fun getLikes():Int {
        return like
    }

    fun fetchLikes(pushKey: String) {
        infoRetrieve.fetchLikesForPushKey(pushKey) { likes ->
            if (likes != null) {
                Log.d("PdfModel", "Likes for $pushKey: $likes")
                val dataQueue = ArrayBlockingQueue<Int>(1)
                dataQueue.put(likes)
                like = dataQueue.take()
                Log.d("PdfModelLike1", like.toString())
            }
            else {
                Log.d("PdfModel", "No file found for $pushKey")
            }
        }

        Log.d("PdfModelLike", like.toString())
    }
    // update Likes takes in a pushKey of the current file, the current user id, and a boolean to represent if you want to increase the like or decrease it
    fun updateLikes(pushKey: String, userId: String, increment: Boolean) {
        infoRetrieve.updateLikesBasedOnPushkey(pushKey, userId, increment)
    }
    // update Collects takes in a pushKey of the current file, the current user id, and a boolean to represent if you want to save to collects or delete from it
    fun updateCollects(pushKey: String, userId: String, increment: Boolean) {
        infoRetrieve.updateCollectsBasedOnPushkey(pushKey, userId, increment)
    }

}