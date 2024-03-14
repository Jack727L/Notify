package com.example.notify.Services.UploadService

import android.net.Uri
import androidx.compose.runtime.MutableState

interface FileUpload {
    fun uploadPdfFileToFirebase(toastGenerated: MutableState<Boolean>,
                                toastMsg: MutableState<String>,
                                uploadProgress: MutableState<Float>,
                                fileName: String?,
                                pdfFileUri: Uri?,
                                subject: String,
                                courseNum: String,
                                term: String,
                                year: String)
}