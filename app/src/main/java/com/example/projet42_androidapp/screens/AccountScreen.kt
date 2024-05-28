package com.example.projet42_androidapp.screens

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projet42_androidapp.viewmodel.AccountViewModel

@Composable
fun AccountScreen(viewModel: AccountViewModel = viewModel()) {
    if (viewModel.isUserLoggedIn.value) {
        AccountInfoScreen(
            viewModel = viewModel,
            onLogoutClick = { viewModel.isUserLoggedIn.value = false },
            onEditInfoClick = { /* Ajouter la logique de modification des informations ici */ },
            onViewEventsClick = { /* Ajouter la logique de visualisation des événements notés ici */ },
            onDeleteAccountClick = { viewModel.isUserLoggedIn.value = false }
        )
    } else {
        if (viewModel.showLoginForm.value) {
            LoginScreen(
                onLoginClick = { viewModel.isUserLoggedIn.value = true },
                onRegisterClick = { viewModel.showLoginForm.value = false }
            )
        } else {
            RegisterScreen(
                onRegisterClick = { viewModel.isUserLoggedIn.value = true },
                onLoginClick = { viewModel.showLoginForm.value = true }
            )
        }
    }
}