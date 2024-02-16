package com.example.notify.ui

import SettingsScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.notify.ui.loginScreen.LoginScreen
import com.example.notify.ui.loginScreen.SignUpScreen
import com.example.notify.ui.search.SearchScreen
import com.example.notify.ui.profile.ProfileScreen


sealed class Route {
    data class LoginScreen(val name:String = "Login"): Route()
    data class SignUpScreen(val name:String = "Signup"): Route()
    data class SearchScreen(val name:String = "Search"): Route()
    data class ProfileScreen(val name:String = "Profile"): Route()
    data class SettingsScreen(val name:String = "Settings"): Route()

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
        composable(route = Route.SearchScreen().name) {
            SearchScreen(
                onBackClick = {
                    navHostController.navigate(
                        Route.LoginScreen().name
                    )
                }
            )
        }
        composable(route = Route.ProfileScreen().name) {
            ProfileScreen(
            )
        }
        composable(route = Route.SettingsScreen().name) {
            SettingsScreen(
            )
        }
    }
}
