package com.example.notify.Services.UploadService

import android.graphics.Bitmap

data class PdfFile(
    val fileName: String, val downloadUrl: String, val uid: String,
    val subject: String, val courseNum: String, val term: String, val year: String, val likes: Int, val collects: Int, val uuid: String, val pushKey: String, val extractedText: String, val firstPageByteArray: ByteArray
) {
    constructor() : this("", "", "","","","","",0, 0, "", "", "", ByteArray(0))
}