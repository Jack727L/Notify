package com.example.notify.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.notify.Services.UploadService.PdfFile
import com.example.notify.Services.UploadService.PdfFilesRetrievalCallback
import com.example.notify.Services.fileRetrieve.fileInfo

class ProfileScreenModel: ViewModel() {
    private val infoRetrieve = fileInfo()
    private val _pdfFiles = MutableLiveData<List<PdfFile>>()

    val pdfFiles: LiveData<List<PdfFile>> = _pdfFiles

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message
    // the intake will be the current user id, which returns all of the files that the user current liked
    fun retrieveUserPdfFiles(userId: String, like_or_collect: String) {
        infoRetrieve.retrieveUserCollectedPdfFiles(userId, like_or_collect, object : PdfFilesRetrievalCallback {
            override fun onSuccess(pdfFiles: List<PdfFile>) {
                if (pdfFiles.isNotEmpty()) {
                    _message.postValue("Collected PDF files retrieved successfully!")
                    _pdfFiles.postValue(pdfFiles)
                    // Optional: Log each retrieved PDF file name
                     pdfFiles.forEach { pdfFile ->
                         Log.d("YourViewModel", "Retrieved PDF File: ${pdfFile.fileName}")
                     }
                } else {
                    _message.postValue("No collected PDF files found.")
                    Log.d("YourViewModel", "No collected PDF files found.")
                }
            }

            override fun onError(errorMessage: String) {
                _message.postValue("Error retrieving collected PDF files: $errorMessage")
                Log.e("YourViewModel", "Error retrieving collected PDF files: $errorMessage")
            }
        })
    }
}