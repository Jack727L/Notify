package com.example.notify.Services.UploadService

data class PdfFile(val fileName: String, val downloadUrl : String, val uid: String,
                   val subject: String, val courseNum: String, val term: String, val year: String, val likes: Int, val uuid: String) {
    constructor() : this("", "", "","","","","",0, "")
}