package com.example.projet42_androidapp.screens

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projet42_androidapp.viewmodel.AccountViewModel

@Composable
fun AccountScreen(viewModel: AccountViewModel = viewModel()) {
    if (viewModel.isUserLoggedIn.value) {
        // Afficher les informations du compte de l'utilisateur
        Text(text = "User Account Information")
    } else {
        if (viewModel.showLoginForm.value) {
            LoginScreen(
                onLoginClick = { /* Ajouter la logique de connexion ici */ },
                onRegisterClick = { viewModel.showLoginForm.value = false }
            )
        } else {
            RegisterScreen(
                onRegisterClick = { /* Ajouter la logique d'inscription ici */ },
                onLoginClick = { viewModel.showLoginForm.value = true }
            )
        }
    }
}