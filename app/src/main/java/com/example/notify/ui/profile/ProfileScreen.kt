package com.example.notify.ui.profile

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.notify.R
import com.example.notify.Services.UploadService.PdfFile
import com.example.notify.ui.home.Bottom
import com.example.notify.ui.home.NoteList
import com.example.notify.ui.theme.Black

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(id: String, currentUserId: String, currentDisplay: String, navController: NavHostController) {
    val uiBackgroundColor = if (isSystemInDarkTheme()) Color.Black else Color.White
    val profileScreenModel: ProfileScreenModel = viewModel()
    when (currentDisplay) {
        "likes" -> {
            LaunchedEffect(Unit) {
                profileScreenModel.retrieveUserPdfFiles(id, "likes")
            }
        }
        "collects" -> {
            LaunchedEffect(Unit) {
                profileScreenModel.retrieveUserPdfFiles(id, "collects")
            }
        }
        else -> {
            LaunchedEffect(Unit) {
                profileScreenModel.retrieveUserPdfFiles(id, "posts")
            }
        }
    }
    val pdfFiles by profileScreenModel.pdfFiles.observeAsState(initial = emptyList())
    Box {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Top(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .background(Color(0xFFBBDEFB)),
                    userName = "John Doe",
                    userId = id,
                    currentUserId = currentUserId
                )
            },
            bottomBar = {
                Bottom(currentUserId = currentUserId, navController = navController)
            }
        ) { paddingValues ->
            Center(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues)
                    .background(uiBackgroundColor),
                navController = navController,
                pdfFiles = pdfFiles,
                currentUserId = currentUserId,
                currentDisplay = currentDisplay,
                userId = id
            )
        }
    }
}

@Composable
fun Center(modifier: Modifier=Modifier, navController: NavHostController, pdfFiles: List<PdfFile>,
           currentUserId: String, currentDisplay: String, userId: String) {

    val uiColor = if (isSystemInDarkTheme()) Color.White else Black
    val textModifier = Modifier.drawBehind {
        val strokeWidthPx = 1.dp.toPx()
        val verticalOffset = size.height - 2.sp.toPx()
        drawLine(
            color = Color(0xFFB3E5FC),
            strokeWidth = strokeWidthPx,
            start = Offset(0f, verticalOffset),
            end = Offset(size.width, verticalOffset)
        )
    }
    Column(modifier) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                ),
                modifier=Modifier
                    .padding(start=36.dp),
                onClick = {navController.navigate("Profile/$userId/$currentUserId/posts")}
            ) {
                Text(modifier = if (currentDisplay == "posts") textModifier else Modifier,
                    fontSize=16.sp, color = uiColor, text="Posts")
            }
            Spacer(Modifier.weight(1f))
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                onClick = {navController.navigate("Profile/$userId/$currentUserId/likes")}
            ) {
                Text(modifier = if (currentDisplay == "likes") textModifier else Modifier,
                    fontSize=16.sp, color = uiColor, text="Likes")
            }
            Spacer(Modifier.weight(1f))
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                modifier=Modifier
                    .padding(end=36.dp),
                onClick = {navController.navigate("Profile/$userId/$currentUserId/collects")}
            ) {
                Text(modifier = if (currentDisplay == "collects") textModifier else Modifier,
                    fontSize=16.sp, color = uiColor, text="Collects")
            }
        }
        pdfFiles.forEach { pdfFile ->
            Log.d("ProfileTest", pdfFile.fileName)
        }
        Box() {
            NoteList(pdfFiles, navController, currentUserId)
        }


    }
}


@Composable
fun Top(modifier: Modifier = Modifier, userName:String, userId:String, currentUserId: String) {
    val uiColor = if (isSystemInDarkTheme()) Color.White else Black
    Column(modifier = modifier,
        verticalArrangement = Arrangement.Center)
    {
        Row(
            modifier = Modifier.padding(start = 20.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically

        ){
            Image(
                painter = painterResource(id = R.drawable.baseline_face_2_24),
                contentDescription = "Big Elephant",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .border(5.dp, Color.LightGray, CircleShape)
            )
            Text(fontSize=24.sp, color=uiColor, text=userName,
                modifier=Modifier.padding(start=10.dp))
        }
    }
}

