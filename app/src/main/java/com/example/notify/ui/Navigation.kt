package com.example.notify.ui

import SettingsScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.notify.ui.home.HomePage
import com.example.notify.ui.login.LoginScreen
import com.example.notify.ui.note.NoteScreen
import com.example.notify.ui.profile.ProfileScreen
import com.example.notify.ui.search.SearchScreen
import com.example.notify.ui.signup.SignUpScreen
import com.example.notify.ui.upload.UploadScreen


sealed class Route {
    data class LoginScreen(val name:String = "Login"): Route()
    data class SignUpScreen(val name:String = "Signup"): Route()
    data class SearchScreen(val name:String = "Search"): Route()
    data class HomeScreen(val name:String = "Home"): Route()
    data class ProfileScreen(val name:String = "Profile"): Route()
    data class SettingsScreen(val name:String = "Setting"): Route()
    data class NoteScreen(val name:String = "Note"): Route()
    data class UploadScreen(val name:String = "Upload"): Route()

}
@Composable
fun Navigation(navHostController: NavHostController) {
    NavHost(navController = navHostController, startDestination = Route.LoginScreen().name) {
        composable(route = Route.HomeScreen().name) {
            HomePage(navHostController)
        }
        composable(route = Route.LoginScreen().name) {
            LoginScreen(
                onSignUpClick = {
                    navHostController.navigate(
                        Route.SignUpScreen().name
                    )
                },
                onLoginClick = {
                    navHostController.navigate(
                        Route.HomeScreen().name
                    )
                }
            )
        }
        composable(route = Route.SignUpScreen().name) {
            SignUpScreen(navHostController)
        }
        composable(route = Route.SearchScreen().name) {
            SearchScreen(
                onBackClick = {
                    navHostController.navigate(
                        Route.HomeScreen().name
                    )
                }
            )
        }
        composable(route = Route.ProfileScreen().name) {
            ProfileScreen()
        }
        composable(route = Route.SettingsScreen().name) {
            SettingsScreen(
            )
        }
        composable(
            route = Route.NoteScreen().name+"/{id}/{downloadUrl}",
            arguments = listOf(
                navArgument("id") { type = NavType.StringType},
                navArgument("downloadUrl") { type = NavType.StringType},
            )
        )
        {args ->
            val id = args.arguments?.getString("id")
            val downloadUrl = args.arguments?.getString("downloadUrl")
            NoteScreen(id, downloadUrl, navHostController)
        }
        composable(route = Route.UploadScreen().name) {
            UploadScreen(navHostController)
        }
    }
}
