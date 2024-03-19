package com.example.notify.Services.UploadService

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
        year: String,
        uid: String,
        uuid: String
    ) {

        var metadata = storageMetadata {
            setCustomMetadata("uid", uid)
            contentType = "application/pdf"
            setCustomMetadata("subject", subject)
            setCustomMetadata("courseNum", courseNum)
            setCustomMetadata("term", term)
            setCustomMetadata("year", year)
            setCustomMetadata("uuid", uuid)
        }

        pdfFileUri?.let { uri ->
            val mStorageRef = storageReference.child("/$subject$courseNum/$uid/$fileName")
            val fdbRef = databaseReference.child("$subject$courseNum")
            mStorageRef.putFile(uri, metadata).addOnSuccessListener {
                mStorageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    fdbRef.push().key?.let { pushKey ->
                        val pdfFile = PdfFile(fileName.orEmpty(), downloadUri.toString(), uid, subject, courseNum, term, year, 0, uuid, pushKey)
                        fdbRef.child(pushKey).setValue(pdfFile)
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
    fun retrieveAllPdfFiles(callback: PdfFilesRetrievalCallback) {
        val databaseReference = FirebaseDatabase.getInstance().reference.child("pdfs/MATH235")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tempList = mutableListOf<PdfFile>()
                snapshot.children.forEach { childSnapshot ->
                    val pdfFile = childSnapshot.getValue(PdfFile::class.java)
                    pdfFile?.let {
                        tempList.add(it)
                    }
                }
                if (tempList.isEmpty()) {
                    Log.e("retrieving", "No Data Found")
                    callback.onError("No Data Found")
                } else {
                    callback.onSuccess(tempList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("retrieving", "Error retrieving data", error.toException())
                callback.onError(error.toException().message ?: "Error retrieving data")
            }
        })
    }
}

