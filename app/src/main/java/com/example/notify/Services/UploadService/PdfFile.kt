package com.example.notify.Services.UploadService

data class PdfFile(val fileName: String, val downloadUrl : String) {
    constructor() : this("", "")
}