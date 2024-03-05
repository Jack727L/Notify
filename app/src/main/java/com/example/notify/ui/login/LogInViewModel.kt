package com.example.notify.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notify.Services.AuthService.Authentication
import com.example.notify.Services.AuthService.Utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogInViewModel @Inject constructor (
    private val auth: Authentication
): ViewModel()
{
    val _logInState = Channel<LogInState>()
    val logInState = _logInState.receiveAsFlow()

    fun loginUser(email:String, password:String) = viewModelScope.launch {
        auth.loginUser(email,password).collect{
            result -> when(result) {
                is Resource.Success -> {
                    _logInState.send(LogInState(isSuccess = "Sign In Success "))
                }
                is Resource.Loading ->{
                    _logInState.send(LogInState(isLoading = true))
                }
                is Resource.Error ->{
                    _logInState.send(LogInState(isError = result.message))
                }
            }
        }
    }
}