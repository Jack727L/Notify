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
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

class SearchModel(private val fileUploadService: FileUploadImpl) : ViewModel() {
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

//    private val _notes = MutableStateFlow(dummyNotes)
    @OptIn(FlowPreview::class)
//    val notes = searchText
//        .debounce(500L)
//        .onEach {_isSearching.update{true}}
//        .combine(_notes) { text, notes ->
//            if (text.isBlank()) {
//                notes
//            } else {
//                delay(1000)
//                notes.filter {
//                    it.doesMatchSearchQuery(text)
//                }
//            }
//        }
//        .onEach {_isSearching.update{false}}
//        .stateIn(
//            viewModelScope,
//            SharingStarted.WhileSubscribed(5000),
//            _notes.value
//        )
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
    val filteredPdfFiles = searchText
        .debounce(500L)
        .onEach {
            _isSearching.value = true  // Set isSearching to true at the start of each search/filter operation
        }
        .combine(_pdfFiles) { text, files ->
            if (text.isBlank()) {
                files
            } else {
                delay(1000)  // Simulate a search delay if necessary
                files.filter { pdfFile ->
                    // Include additional attributes in the search criteria
                    pdfFile.fileName.contains(text, ignoreCase = true) ||
                            pdfFile.year.contains(text, ignoreCase = true) ||
                            pdfFile.term.contains(text, ignoreCase = true) ||
                            pdfFile.subject.contains(text, ignoreCase = true) ||
                            pdfFile.courseNum.contains(text, ignoreCase = true)
                }

            }
        }
        .onEach {
            _isSearching.value = false  // Set isSearching back to false once filtering is done
        }
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


//data class Note(
//    val term: String,
//    val courseCode: String,
//) {
//    fun doesMatchSearchQuery(query: String): Boolean {
//        val matchingCombinations = listOf(
//            "$term$courseCode",
//            "$term $courseCode",
//            "${term.first()} ${courseCode.first()}"
//        )
//
//        return matchingCombinations.any {
//            it.contains(query, ignoreCase=true)
//        }
//    }
//}
//
//private val dummyNotes = listOf(
//    Note(
//        term = "2024WINTER",
//        courseCode = "STAT330"
//    ),
//    Note(
//        term = "2024WINTER",
//        courseCode = "STAT333"
//    ),
//    Note(
//        term = "2024WINTER",
//        courseCode = "CS240"
//    ),
//    Note(
//        term = "2024WINTER",
//        courseCode = "MATH235"
//    ),
//    Note(
//        term = "2024WINTER",
//        courseCode = "CS251"
//    ),
//    Note(
//        term = "2024WINTER",
//        courseCode = "CS346"
//    ),
//)