package com.example.notify.ui.note

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.notify.R
import com.example.notify.databinding.PdfViewBinding
import com.example.notify.ui.profile.ProfileScreenModel
import com.example.notify.ui.theme.Black
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.URL

@Composable
fun NoteScreen(id: String, downloadUrl: String, pushKey: String, userId: String, navController: NavHostController) {
    // this save to collects
    val noteScreenModel: NoteScreenModel = viewModel()
    var input: InputStream? by remember { mutableStateOf(null) }
    val profileScreenModel: ProfileScreenModel = viewModel()

    LaunchedEffect(Unit){
        withContext(Dispatchers.IO) {
            val temp = URL(downloadUrl).openStream()
            input = temp
        }
    }
    input?.let {
        PdfView(downloadUrl = it, pushKey = pushKey, id = id, navController=navController,
        noteScreenModel=noteScreenModel, profileScreenModel=profileScreenModel, userId = userId)
    }
}

@Composable
fun PdfView(
    downloadUrl: InputStream,
    pushKey: String,
    id: String,
    navController: NavHostController,
    noteScreenModel: NoteScreenModel,
    profileScreenModel: ProfileScreenModel,
    userId: String
) {
    Box {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(fraction = 0.45f),
            painter = painterResource(id = R.drawable.shape),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )
        Scaffold(
            containerColor = Color.Transparent,
            topBar = { TopSection(navController = navController, id = id, currentUserId = userId) },
            bottomBar = { Bottom (modifier = Modifier.fillMaxWidth(), profileScreenModel, pushKey, id, userId, noteScreenModel) }
        ) { paddingValues ->
            Center(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues)
                    .background(Color.Transparent),
                downloadUrl = downloadUrl
            )
        }
    }

}

@Composable
private fun Center(modifier: Modifier=Modifier, downloadUrl: InputStream?) {
    if (downloadUrl != null) {
        Box(modifier) {
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(0.dp, 10.dp, 0.dp, 10.dp),
                factory = { context ->
                    PdfViewBinding.inflate(LayoutInflater.from(context))
                        .apply {
                            pdfView.fromStream(downloadUrl).load()
                        }
                        .root
                },
            )
        }
    }
}


@Composable
private fun Bottom(modifier:Modifier = Modifier, profileScreenModel: ProfileScreenModel, pushKey: String, id: String, currentUserId: String, noteScreenModel:NoteScreenModel) {
    var favorite by remember { mutableIntStateOf(0) }
    var collect by remember { mutableIntStateOf(0) }

//    profileScreenModel.retrieveUserPdfFiles(id, "collects")
//    val collectedFiles by profileScreenModel.pdfFiles.observeAsState(initial = emptyList())
//    run breaking@ {
//        collectedFiles.forEach{pdfFile ->
//            if (pdfFile.pushKey == pushKey) {
//                isCollect = true
//                return@breaking
//            } else {
//                isCollect = false
//            }
//        }
//    }

    noteScreenModel.fetchLikes(pushKey)
    favorite = noteScreenModel.getLikes()

    noteScreenModel.fetchCollects(pushKey)
    collect = noteScreenModel.getCollects()
    val uiColor = if (isSystemInDarkTheme()) Color.White else Black

    Box(modifier=modifier) {
        Row(modifier=Modifier.align(Alignment.CenterEnd),
            verticalAlignment = Alignment.CenterVertically) {
            FavoriteButton(
                addFavorite={
                            noteScreenModel.updateLikes(pushKey, currentUserId, true)
                            noteScreenModel.fetchLikes(pushKey)
                            Handler(Looper.getMainLooper()).postDelayed({
                                favorite = noteScreenModel.getLikes()
                                }, 100)
                            },
                subFavorite={
                    noteScreenModel.updateLikes(pushKey, currentUserId, false)
                    noteScreenModel.fetchLikes(pushKey)
                    Handler(Looper.getMainLooper()).postDelayed({
                        favorite = noteScreenModel.getLikes()
                    }, 100)
                },
                color=uiColor,
                profileScreenModel = profileScreenModel,
                id = currentUserId,
                pushKey = pushKey
            )
            Text(favorite.toString(), color=uiColor, modifier=Modifier.padding(end=5.dp))
            CollectButton(
                addCollects={
                    noteScreenModel.updateCollects(pushKey, currentUserId, true)
                    noteScreenModel.fetchCollects(pushKey)
                    Handler(Looper.getMainLooper()).postDelayed({
                        collect = noteScreenModel.getCollects()
                    }, 100)
                },
                subCollects={
                    noteScreenModel.updateCollects(pushKey, currentUserId, false)
                    noteScreenModel.fetchCollects(pushKey)
                    Handler(Looper.getMainLooper()).postDelayed({
                        collect = noteScreenModel.getCollects()
                    }, 100)
                },
                color=uiColor
            )
            Text(collect.toString(), color=uiColor, modifier=Modifier.padding(end=20.dp))
        }
    }
}

@Composable
private fun TopSection(navController: NavHostController, id: String,
                       currentUserId: String) {
    val uiColor = if (isSystemInDarkTheme()) Color.White else Black
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, end = 16.dp),
        contentAlignment = Alignment.TopStart
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal=16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Button(
                onClick= { navController.popBackStack() },
                modifier= Modifier
                    .height(40.dp)
                    .width(40.dp)
                    .padding(end=16.dp),
                contentPadding = PaddingValues(1.dp),
                shape= RectangleShape,
                colors = ButtonDefaults.buttonColors(Color.Transparent)
            ) {
                Icon(
                    Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = null,
                    tint=uiColor
                )
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color = Color.White, shape = CircleShape)
                    .clickable { navController.navigate("profile/$id/$currentUserId") }
            )
            if (id == currentUserId) {
                Spacer(Modifier.weight(1f))
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    ),
                    onClick = {}
                ) {
                    Text("Delete")
                }
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            offset = DpOffset(x = (-8).dp, y = 5.dp)
        ) {
            DropdownMenuItem(
                text = { Text("Profile") },
                onClick = {
                    expanded = false
                    navController.navigate("profile")
                }
            )
            DropdownMenuItem(
                text = { Text("Setting") },
                onClick = {
                    expanded = false
                    navController.navigate("setting")
                }
            )
            DropdownMenuItem(
                text = { Text("Log Out") },
                onClick = {
                    expanded = false
                    navController.navigate("Login")
                }
            )
        }
    }
}

@Composable
fun FavoriteButton(
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    addFavorite: ()->Unit,
    subFavorite: ()->Unit,
    profileScreenModel: ProfileScreenModel,
    id: String,
    pushKey: String
) {
    var isFavorite by remember { mutableStateOf(false) }
    profileScreenModel.retrieveUserPdfFiles(id, "likes")
    val likedFiles by profileScreenModel.pdfFiles.observeAsState(initial = emptyList())
    run breaking@ {
        likedFiles.forEach{pdfFile ->
            if (pdfFile.pushKey == pushKey) {
                isFavorite = true
                return@breaking
            } else {
                isFavorite = false
            }
        }
    }
    IconToggleButton(
        checked = isFavorite,
        onCheckedChange = {
            if (isFavorite) {
                subFavorite()
            } else {
                addFavorite()
            }
            isFavorite = !isFavorite
        }
    ) {
        Icon(
            tint = color,
            imageVector = if (isFavorite) {
                Icons.Filled.Favorite
            } else {
                Icons.Default.FavoriteBorder
            },
            contentDescription = null
        )
    }

}

@Composable
fun CollectButton(
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    addCollects: ()->Unit,
    subCollects: ()->Unit,
) {
    var isCollect by remember { mutableStateOf(false) }

    IconToggleButton(
        checked = isCollect,
        onCheckedChange = {
            if (isCollect) {
                addCollects()
            } else {
                subCollects()
            }
            isCollect = !isCollect
        },
    ) {
        Icon(
            tint = color,
            imageVector = if (isCollect) {
                Icons.Filled.Star
            } else {
                Icons.Outlined.Star
            },
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
    }

}
@Composable
fun starBorder(color: Color): ImageVector {
    return remember {
        ImageVector.Builder(
            name = "star",
            defaultWidth = 40.0.dp,
            defaultHeight = 40.0.dp,
            viewportWidth = 40.0f,
            viewportHeight = 40.0f
        ).apply {
            path(
                fill = SolidColor(color),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(13.667f, 33.958f)
                quadToRelative(-1.042f, 0.75f, -2.084f, 0.021f)
                quadToRelative(-1.041f, -0.729f, -0.625f, -1.979f)
                lineToRelative(2.417f, -7.875f)
                lineToRelative(-6.208f, -4.458f)
                quadToRelative(-1.084f, -0.75f, -0.709f, -1.979f)
                quadToRelative(0.375f, -1.23f, 1.667f, -1.23f)
                horizontalLineToRelative(7.708f)
                lineToRelative(2.5f, -8.25f)
                quadToRelative(0.167f, -0.625f, 0.646f, -0.937f)
                quadToRelative(0.479f, -0.313f, 1.021f, -0.313f)
                quadToRelative(0.542f, 0f, 1.021f, 0.313f)
                quadToRelative(0.479f, 0.312f, 0.687f, 0.937f)
                lineToRelative(2.459f, 8.25f)
                horizontalLineToRelative(7.708f)
                quadToRelative(1.292f, 0f, 1.667f, 1.23f)
                quadToRelative(0.375f, 1.229f, -0.709f, 1.979f)
                lineToRelative(-6.208f, 4.458f)
                lineTo(29.083f, 32f)
                quadToRelative(0.375f, 1.25f, -0.666f, 1.979f)
                quadToRelative(-1.042f, 0.729f, -2.084f, -0.062f)
                lineToRelative(-6.291f, -4.792f)
                close()
            }
        }.build()
    }
}

@Composable
fun starFilled(color: Color): ImageVector {
    return remember {
        ImageVector.Builder(
            name = "star",
            defaultWidth = 40.0.dp,
            defaultHeight = 40.0.dp,
            viewportWidth = 40.0f,
            viewportHeight = 40.0f
        ).apply {
            path(
                fill = SolidColor(color),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(13.667f, 33.958f)
                quadToRelative(-1.042f, 0.75f, -2.084f, 0.021f)
                quadToRelative(-1.041f, -0.729f, -0.625f, -1.979f)
                lineToRelative(2.417f, -7.875f)
                lineToRelative(-6.208f, -4.458f)
                quadToRelative(-1.084f, -0.75f, -0.709f, -1.979f)
                quadToRelative(0.375f, -1.23f, 1.667f, -1.23f)
                horizontalLineToRelative(7.708f)
                lineToRelative(2.5f, -8.25f)
                quadToRelative(0.167f, -0.625f, 0.646f, -0.937f)
                quadToRelative(0.479f, -0.313f, 1.021f, -0.313f)
                quadToRelative(0.542f, 0f, 1.021f, 0.313f)
                quadToRelative(0.479f, 0.312f, 0.687f, 0.937f)
                lineToRelative(2.459f, 8.25f)
                horizontalLineToRelative(7.708f)
                quadToRelative(1.292f, 0f, 1.667f, 1.23f)
                quadToRelative(0.375f, 1.229f, -0.709f, 1.979f)
                lineToRelative(-6.208f, 4.458f)
                lineTo(29.083f, 32f)
                quadToRelative(0.375f, 1.25f, -0.666f, 1.979f)
                quadToRelative(-1.042f, 0.729f, -2.084f, -0.062f)
                lineToRelative(-6.291f, -4.792f)
                close()
            }
        }.build()
    }
}

@Composable
fun rememberscanDelete(color: Color = Color.White): ImageVector {
    return remember {
        ImageVector.Builder(
            name = "scan_delete",
            defaultWidth = 40.0.dp,
            defaultHeight = 40.0.dp,
            viewportWidth = 40.0f,
            viewportHeight = 40.0f
        ).apply {
            path(
                fill = SolidColor(color),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(234.985f, 251.652f)
                verticalLineToRelative(186f)
                verticalLineToRelative(-186f)
                verticalLineToRelative(648.696f)
                verticalLineToRelative(-8.283f)
                verticalLineToRelative(8.283f)
                verticalLineToRelative(-648.696f)
                close()
                moveToRelative(0f, 733.508f)
                quadToRelative(-35.064f, 0f, -59.938f, -24.874f)
                quadToRelative(-24.874f, -24.874f, -24.874f, -59.938f)
                verticalLineTo(251.652f)
                quadToRelative(0f, -35.22f, 24.874f, -60.204f)
                quadToRelative(24.874f, -24.985f, 59.938f, -24.985f)
                horizontalLineToRelative(310.233f)
                quadToRelative(17.34f, 0f, 33.054f, 6.79f)
                quadToRelative(15.714f, 6.79f, 27.869f, 18.585f)
                lineToRelative(178.162f, 178.047f)
                quadToRelative(12.154f, 12.129f, 19.027f, 27.897f)
                quadToRelative(6.874f, 15.768f, 6.874f, 33.167f)
                verticalLineToRelative(194.842f)
                quadToRelative(-19.711f, -9.435f, -40.873f, -14.946f)
                quadToRelative(-21.162f, -5.511f, -44.316f, -6.845f)
                verticalLineTo(437.652f)
                horizontalLineTo(622.204f)
                quadToRelative(-35.72f, 0f, -60.455f, -24.735f)
                quadToRelative(-24.734f, -24.734f, -24.734f, -60.454f)
                verticalLineTo(251.652f)
                horizontalLineToRelative(-302.03f)
                verticalLineToRelative(648.696f)
                horizontalLineToRelative(267.457f)
                quadToRelative(7.203f, 24.261f, 19.993f, 45.642f)
                quadToRelative(12.79f, 21.38f, 29.826f, 39.17f)
                horizontalLineTo(234.985f)
                close()
                moveToRelative(477.761f, -95.681f)
                lineToRelative(-54.463f, 54.029f)
                quadToRelative(-12.87f, 12.036f, -29.687f, 11.909f)
                quadToRelative(-16.817f, -0.126f, -29.354f, -12.796f)
                quadToRelative(-13.467f, -12.781f, -13.467f, -29.664f)
                quadToRelative(0f, -16.884f, 13.325f, -30.264f)
                lineToRelative(53.508f, -54.037f)
                lineToRelative(-54.573f, -54.323f)
                quadToRelative(-12.825f, -12.448f, -12.543f, -29.354f)
                quadToRelative(0.283f, -16.906f, 13.551f, -30.341f)
                quadToRelative(12.892f, -13.058f, 29.714f, -13.058f)
                quadToRelative(16.823f, 0f, 30.203f, 12.948f)
                lineToRelative(53.786f, 54.573f)
                lineToRelative(54.573f, -54.573f)
                quadToRelative(12.26f, -12.26f, 29.492f, -12.06f)
                quadToRelative(17.231f, 0.199f, 30.192f, 13.052f)
                quadToRelative(12.381f, 12.553f, 12.215f, 29.47f)
                quadToRelative(-0.167f, 16.916f, -12.203f, 29.453f)
                lineToRelative(-54.029f, 54.463f)
                lineToRelative(54.029f, 54.464f)
                quadToRelative(12.036f, 12.869f, 12.003f, 29.861f)
                quadToRelative(-0.032f, 16.991f, -12.886f, 29.862f)
                quadToRelative(-12.552f, 12.219f, -29.469f, 12.335f)
                quadToRelative(-16.917f, 0.116f, -29.453f, -11.92f)
                lineToRelative(-54.464f, -54.029f)
                close()
            }
        }.build()
    }
}