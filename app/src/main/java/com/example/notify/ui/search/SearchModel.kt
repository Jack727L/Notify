package com.example.notify.ui.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.notify.Services.UploadService.FileUploadImpl
import com.example.notify.Services.UploadService.PdfFile
import com.example.notify.Services.UploadService.PdfFilesRetrievalCallback
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class SearchModel(private val fileUploadService: FileUploadImpl) : ViewModel() {
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _notes = MutableStateFlow(dummyNotes)
    @OptIn(FlowPreview::class)
    val notes = searchText
        .debounce(500L)
        .onEach {_isSearching.update{true}}
        .combine(_notes) { text, notes ->
            if (text.isBlank()) {
                notes
            } else {
                delay(1000)
                notes.filter {
                    it.doesMatchSearchQuery(text)
                }
            }
        }
        .onEach {_isSearching.update{false}}
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _notes.value
        )
    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }
    private val _pdfFiles = MutableStateFlow<List<PdfFile>>(emptyList())
    init {
        val storageReference = FirebaseStorage.getInstance().reference
        val databaseReference = FirebaseDatabase.getInstance().getReference("pdfs/MATH235")
        val fileUploadService = FileUploadImpl(storageReference, databaseReference)

        fileUploadService.retrieveAllPdfFiles(object : PdfFilesRetrievalCallback {
            override fun onSuccess(pdfFiles: List<PdfFile>) {
                _pdfFiles.value = pdfFiles // Update the state flow directly
                pdfFiles.forEach { pdfFile ->
                    Log.d("SearchModel", "Retrieved PDF File: ${pdfFile.fileName}")
                }
            }

            override fun onError(errorMessage: String) {
                Log.e("SearchModel", "Error retrieving PDF files: $errorMessage")
            }
        })
    }
    val filteredPdfFiles = searchText
        .debounce(500L)
        .onEach { _isSearching.value = true }
        .combine(_pdfFiles) { text, files ->
            if (text.isBlank()) files
            else files.filter { it.fileName.contains(text, ignoreCase = true) }
        }
        .onEach { _isSearching.value = false }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
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


data class Note(
    val term: String,
    val courseCode: String,
) {
    fun doesMatchSearchQuery(query: String): Boolean {
        val matchingCombinations = listOf(
            "$term$courseCode",
            "$term $courseCode",
            "${term.first()} ${courseCode.first()}"
        )

        return matchingCombinations.any {
            it.contains(query, ignoreCase=true)
        }
    }
}

private val dummyNotes = listOf(
    Note(
        term = "2024WINTER",
        courseCode = "STAT330"
    ),
    Note(
        term = "2024WINTER",
        courseCode = "STAT333"
    ),
    Note(
        term = "2024WINTER",
        courseCode = "CS240"
    ),
    Note(
        term = "2024WINTER",
        courseCode = "MATH235"
    ),
    Note(
        term = "2024WINTER",
        courseCode = "CS251"
    ),
    Note(
        term = "2024WINTER",
        courseCode = "CS346"
    ),
)