package com.example.notify.ui.upload


import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import com.example.notify.Services.UploadService.FileUpload
import com.example.notify.Services.UserService.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UploadViewModel @Inject constructor (
    private val fileUpload: FileUpload,
    private val user: User
): ViewModel() {
    var pdfFileUri: Uri? = null
    var fileName: String? = null
    fun UploadPdfFileToFirebase(
        displayToast: MutableState<Boolean>,
        toastMsg: MutableState<String>,
        uploadProgress: MutableState<Float>,
        courseNum: String,
        subject: String,
        term: String,
        year: String
        ) {
        fileUpload.uploadPdfFileToFirebase(displayToast, toastMsg, uploadProgress, fileName,pdfFileUri, subject, courseNum,
            term, year, user.getCurrentUserId().orEmpty())
    }
}