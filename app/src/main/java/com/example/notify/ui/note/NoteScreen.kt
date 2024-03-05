package com.example.notify.ui.note

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.github.barteksc.pdfviewer.PDFView

@Composable
fun NoteScreen(id: String?) {
    val context = LocalContext.current
    val filename = "cheat_sheet.pdf"
    val fileInputStream = context.assets.open(filename)
    val byteArray = fileInputStream.readBytes()

    PdfView(byteArray = byteArray)
}

@Composable
fun PdfView(
    byteArray: ByteArray,
) {
    AndroidView(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Red),

        factory = { context ->
            PDFView(context, null)
        },
        update = { pdfView ->
            Log.d(TAG, "PDF view UPDATE called")
            pdfView.fromBytes(byteArray).load()
            Log.d(TAG, "Page Count: ${pdfView.pageCount}")
            Log.d(TAG, String(bytes = byteArray))
        }
    )
}
