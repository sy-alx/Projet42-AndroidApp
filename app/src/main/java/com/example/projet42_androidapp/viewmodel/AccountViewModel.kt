package com.example.projet42_androidapp.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class AccountViewModel : ViewModel() {
    // Simulated user data
    val userFirstName = mutableStateOf("John")
    val userLastName = mutableStateOf("Doe")
    val userEmail = mutableStateOf("johndoe@example.com")
    val userPassword = mutableStateOf("********")

    val isUserLoggedIn = mutableStateOf(true)
    val showLoginForm = mutableStateOf(true)

    // Methods to simulate updating user information
    fun updateFirstName(newFirstName: String) {
        userFirstName.value = newFirstName
    }

    fun updateLastName(newLastName: String) {
        userLastName.value = newLastName
    }

    fun updateEmail(newEmail: String) {
        userEmail.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        userPassword.value = newPassword
    }
}