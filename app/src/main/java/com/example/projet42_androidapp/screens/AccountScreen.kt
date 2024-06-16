package com.example.projet42_androidapp.screens

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projet42_androidapp.viewmodel.AccountViewModel

@Composable
fun AccountScreen(viewModel: AccountViewModel = viewModel(), context: Context) {
    if (viewModel.isUserLoggedIn.value == true) {
        AccountInfoScreen(
            viewModel = viewModel,
            onLogoutClick = { viewModel.isUserLoggedIn.value = false },
            onEditInfoClick = { viewModel.toggleEditMode() },
            onViewEventsClick = { /* Ajouter la logique de visualisation des événements notés ici */ },
            onDeleteAccountClick = { viewModel.isUserLoggedIn.value = false }
        )
    } else {
        if (viewModel.showLoginForm.value == true) {
            LoginScreen(
                context = context,
                onLoginSuccess = { token ->
                    viewModel.isUserLoggedIn.value = true
                    // Stockez le token ou utilisez-le pour les requêtes API
                },
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

