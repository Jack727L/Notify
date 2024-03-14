package com.example.notify.Services.UploadService

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storageMetadata
import javax.inject.Inject


class FileUploadImpl  @Inject constructor (
    private val storageReference: StorageReference,
    private var databaseReference: DatabaseReference,
)
    :FileUpload {
    override fun uploadPdfFileToFirebase(
        toastGenerated: MutableState<Boolean>,
        msg: MutableState<String>,
        uploadProgress: MutableState<Float>,
        fileName: String?,
        pdfFileUri: Uri?,
        subject: String,
        courseNum: String,
        term: String,
        year: String
    ) {

        var metadata = storageMetadata {
            contentType = "application/pdf"
            setCustomMetadata("subject", subject)
            setCustomMetadata("courseNum", courseNum)
            setCustomMetadata("term", term)
            setCustomMetadata("year", year)
        }

        pdfFileUri?.let { uri ->
            val mStorageRef = storageReference.child("${System.currentTimeMillis()}/$fileName")
            mStorageRef.putFile(uri, metadata).addOnSuccessListener {
                mStorageRef.downloadUrl.addOnSuccessListener { downloadUri ->

                    val pdfFile = PdfFile(fileName.orEmpty(), downloadUri.toString())
                    databaseReference.push().key?.let { pushKey ->
                        databaseReference.child(pushKey).setValue(pdfFile)
                            .addOnSuccessListener {
                                toastGenerated.value = true
                                msg.value = "Uploaded Successfully"
                                Log.i("upload", "Success! uploaded file: $fileName to realtime db")
                            }.addOnFailureListener { err ->
                                toastGenerated.value = true
                                msg.value = err.message.toString()
                                Log.e("upload", "Failed to uploaded file: $fileName to realtime db")
                            }
                    }
                }
            }.addOnProgressListener { uploadTask ->
                uploadProgress.value = (uploadTask.bytesTransferred * 100 / uploadTask.totalByteCount).toFloat()
                Log.i("upload", "Uploaded progress ${uploadProgress.value}")
            }.addOnFailureListener { err ->
                toastGenerated.value = true
                msg.value = err.message.toString()
                Log.e("upload", "Failed to upload $fileName to storage")
            }
        }
    }
}

