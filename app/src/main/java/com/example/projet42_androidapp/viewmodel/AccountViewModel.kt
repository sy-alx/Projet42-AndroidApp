package com.example.projet42_androidapp.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class AccountViewModel : ViewModel() {
    val isUserLoggedIn = mutableStateOf(false)
    val showLoginForm = mutableStateOf(true)
}