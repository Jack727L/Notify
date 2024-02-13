package com.example.notify.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.notify.ui.loginScreen.LoginScreen
import com.example.notify.ui.loginScreen.SignUpScreen

sealed class Route {
    data class LoginScreen(val name:String = "Login"): Route()
    data class SignUpScreen(val name:String = "Signup"): Route()

}
@Composable
fun Navigation(navHostController: NavHostController) {
    NavHost(navController = navHostController, startDestination = Route.LoginScreen().name) {
        composable(route = Route.LoginScreen().name) {
            LoginScreen(
                onSignUpClick = {
                    navHostController.navigate(
                        Route.SignUpScreen().name
                    )
                }
            )
        }
        composable(route = Route.SignUpScreen().name) {
            SignUpScreen()
        }
    }
}
