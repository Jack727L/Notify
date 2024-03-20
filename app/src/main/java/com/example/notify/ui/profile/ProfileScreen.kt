package com.example.notify.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.notify.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    // this is the implementation for retrieving all of the collected files, use similar method to pull all of the liked files as well,
    // modify the string to place either "likes" or "collects" to return corresponding information, if the list is null, then it means
    // no files are liked/collected
//    val profileScreenModel: ProfileScreenModel = viewModel()
//    LaunchedEffect(Unit) {
//        profileScreenModel.retrieveUserPdfFiles("3EDy3pXfeAVVDC4gAIdY8ytRGNm1", "likes")
//    }
//    // Tom just use this pdfFiles as the entire object, now it gets loaded automatically, so we are good to use this variable
//    val pdfFiles by profileScreenModel.pdfFiles.observeAsState(initial = emptyList())
//    pdfFiles.forEach { pdfFile ->
//        Log.d("ProfileScreenModel", "Retrieved PDF File: ${pdfFile.fileName}")
//    }
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = stringResource(id = R.string.id)) })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* this is for later to edit profile */ }) {
                Icon(Icons.Filled.Edit, contentDescription = "Edit Profile")
            }
        }
    ) { paddingValues ->
        BodyContent(Modifier.padding(paddingValues))
    }
}

@Composable
fun BodyContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Dummy User", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))

        ProfileDetail("Email", "example@uwaterloo.ca")
        ProfileDetail("Uploaded Documents", "10")
        ProfileDetail("Bookmarked Documents", "5")
        ProfileDetail("Likes amount", "50")
    }
}

@Composable
fun ProfileDetail(label: String, detail: String) {
    Row(modifier = Modifier.padding(vertical = 8.dp)) {
        Text("$label:", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.width(8.dp))
        Text(detail, style = MaterialTheme.typography.bodyMedium)
    }
}

