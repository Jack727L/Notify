package com.example.notify.ui.note

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.notify.Services.fileRetrieve.fileInfo

class NoteScreenModel : ViewModel()  {
    private val infoRetrieve = fileInfo()

    fun fetchLikes(downloadUrl: String) {
        infoRetrieve.fetchLikesForDownloadUrl(downloadUrl) { likes ->
            if (likes != null) {
                Log.d("PdfModel", "Likes for $downloadUrl: $likes")
            } else {
                Log.d("PdfModel", "No file found for $downloadUrl")
            }
        }
    }

    fun incrementLikes(downloadUrl: String) {
        infoRetrieve.updateLikesForDownloadUrl(downloadUrl)
    }

    fun decrementLikes(downloadUrl: String) {
        infoRetrieve.decrementLikesForDownloadUrl(downloadUrl)
    }

}