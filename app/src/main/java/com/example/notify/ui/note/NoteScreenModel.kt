package com.example.notify.ui.note

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.notify.Services.fileRetrieve.fileInfo

class NoteScreenModel : ViewModel()  {
    private val infoRetrieve = fileInfo()

    fun fetchLikes(pushKey: String) {
        infoRetrieve.fetchLikesForPushKey(pushKey) { likes ->
            if (likes != null) {
                Log.d("PdfModel", "Likes for $pushKey: $likes")
            } else {
                Log.d("PdfModel", "No file found for $pushKey")
            }
        }
    }

    fun incrementLikes(pushKey: String) {
        infoRetrieve.incrementLikesBasedOnKey(pushKey)
    }

    fun decrementLikes(pushKey: String) {
        infoRetrieve.decrementLikesBasedOnKey(pushKey)
    }

}