package com.example.notify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.notify.ui.Navigation
import com.example.notify.ui.theme.ComposeLoginScreenInitTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            ComposeLoginScreenInitTheme {
                val navController = rememberNavController()
                Navigation(navController)
            }
        }
    }

}