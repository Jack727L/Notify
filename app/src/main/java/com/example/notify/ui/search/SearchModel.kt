package com.example.notify.ui.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.notify.Services.UploadService.FileUploadImpl
import com.example.notify.Services.UploadService.PdfFile
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

class SearchModel(private val fileUploadService: FileUploadImpl) : ViewModel() {
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()
    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }
    private val _pdfFiles = MutableStateFlow<List<PdfFile>>(emptyList())
    init {
        val storageReference = FirebaseStorage.getInstance().reference
        val databaseReference = FirebaseDatabase.getInstance().getReference("pdfs")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("SearchModel", "Data changed in Firebase")
                val updatedFiles = mutableListOf<PdfFile>()
                snapshot.children.forEach { child ->
                    child.getValue(PdfFile::class.java)?.let { updatedFiles.add(it) }
                }
                _pdfFiles.value = updatedFiles // Update the state flow directly with the new list
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("SearchModel", "Error retrieving PDF files: ${error.message}")
            }
        })
    }
    private val weights = mapOf(
        "fileName" to 5,
        "year" to 1,
        "term" to 2,
        "subject" to 3,
        "courseNum" to 4
    )
    val filteredPdfFiles = searchText
        .debounce(500L)
        .onEach {
            _isSearching.value = true
        }
        .map { query ->
            if (query.isBlank()) {
                _pdfFiles.value
            } else {
                delay(1000)
                _pdfFiles.value
                    .map { pdfFile ->
                        pdfFile to calculateScore(pdfFile, query)
                    }
                    .filter { it.second > 0 }
                    .sortedByDescending { it.second }
                    .map { it.first }
            }
        }
        .onEach {
            _isSearching.value = false  // End search operation
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private fun calculateScore(pdfFile: PdfFile, query: String): Int {
        var score = 0
        query.split(" ").forEach { word ->
            if (pdfFile.fileName.contains(word, ignoreCase = true)) score += weights["fileName"] ?: 0
            if (pdfFile.year.contains(word, ignoreCase = true)) score += weights["year"] ?: 0
            if (pdfFile.term.contains(word, ignoreCase = true)) score += weights["term"] ?: 0
            if (pdfFile.subject.contains(word, ignoreCase = true)) score += weights["subject"] ?: 0
            if (pdfFile.courseNum.contains(word, ignoreCase = true)) score += weights["courseNum"] ?: 0
        }
        return score
    }
}

class SearchModelFactory(private val fileUploadService: FileUploadImpl) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchModel(fileUploadService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
