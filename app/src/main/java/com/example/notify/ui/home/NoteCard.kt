package com.example.notify.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.notify.R
import com.example.notify.Services.UploadService.PdfFile
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun NoteList(pdfFiles: List<PdfFile>, navController: NavHostController,) {
    LazyVerticalGrid(columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(5.dp)) {
        itemsIndexed(pdfFiles) {index, pdfFile ->
            NoteCard(
                id = pdfFile.uid,
                navController = navController,
                contentDescription = "${pdfFile.year}${pdfFile.term} ${pdfFile.subject}${pdfFile.courseNum}",
                title = pdfFile.fileName,
                downloadUrl = pdfFile.downloadUrl,
                likes = pdfFile.likes,
                modifier= Modifier
                    .padding(6.dp)
                    .fillMaxWidth()
            )
        }
    }
}
@Composable
fun NoteCard(
    id: String,
    likes: Int,
    navController: NavHostController,
    contentDescription: String,
    title: String,
    downloadUrl: String,
    modifier: Modifier = Modifier
) {
    val encodedUrl = URLEncoder.encode(downloadUrl, StandardCharsets.UTF_8.toString())
    Card(
        onClick = {navController.navigate(route = "Note/$id/$encodedUrl")},
        modifier = modifier,
        elevation= CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Box(modifier = Modifier.height(300.dp)) {
            Scaffold(
                containerColor = Color.White,
                bottomBar = {
                    Box(modifier = Modifier
                        .height(80.dp)
                        .fillMaxWidth()
                        .padding(10.dp)
                        .background(color=Color.White),
                        contentAlignment = Alignment.BottomStart)
                    {
                        Column() {
                            Text(contentDescription, style= TextStyle(color= Color.Black, fontSize=14.sp))
                            Row() {
                                Text(title, style= TextStyle(color= Color.Black, fontSize=16.sp))

                            }
                        }
                    }
                }
            ) { paddingValues ->
                Box(modifier=Modifier.padding(paddingValues)) {
                    Image(
                        painter=painterResource(id = R.drawable.facebook),
                        contentDescription=contentDescription,
                        contentScale= ContentScale.Crop,
                    )
                }
            }


        }
    }
}
