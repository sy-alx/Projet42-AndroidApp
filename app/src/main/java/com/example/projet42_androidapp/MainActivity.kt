package com.example.projet42_androidapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.projet42_androidapp.screens.AccountInfoScreen
import com.example.projet42_androidapp.screens.AccountScreen
import com.example.projet42_androidapp.screens.EventsScreen
import com.example.projet42_androidapp.screens.HelpScreen
import com.example.projet42_androidapp.screens.HomeScreen
import com.example.projet42_androidapp.ui.theme.Projet42AndroidAppTheme
import com.example.projet42_androidapp.utils.AuthConfig
import com.example.projet42_androidapp.utils.CustomConnectionBuilder
import com.example.projet42_androidapp.viewmodel.AccountViewModel
import net.openid.appauth.AppAuthConfiguration
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration

class MainActivity : ComponentActivity() {
    private lateinit var authService: AuthorizationService
    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configuration du service d'autorisation
        val serviceConfiguration = AuthorizationServiceConfiguration(
            Uri.parse("http://192.168.1.29:8090/realms/projet42-realm/protocol/openid-connect/auth"),
            Uri.parse("http://192.168.1.29:8090/realms/projet42-realm/protocol/openid-connect/token")
        )

        // Configuration AppAuth avec CustomConnectionBuilder
        val appAuthConfig = AppAuthConfiguration.Builder()
            .setConnectionBuilder(CustomConnectionBuilder)
            .build()

        authService = AuthorizationService(this, appAuthConfig)

        setContent {
            Projet42AndroidAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    navController = rememberNavController()
                    MainScreen(context = this, navController = navController)
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_AUTH) {
            if (data != null) {
                val resp = AuthorizationResponse.fromIntent(data)
                val ex = AuthorizationException.fromIntent(data)
                if (resp != null) {
                    val clientAuth = AuthConfig.createClientAuthentication()
                    authService.performTokenRequest(
                        resp.createTokenExchangeRequest(),
                        clientAuth // Ajoutez l'authentification du client ici
                    ) { tokenResponse, exception ->
                        if (tokenResponse != null) {
                            val accessToken = tokenResponse.accessToken
                            // Passez le token à l'écran AccountInfo
                            navController.navigate("account/${accessToken}")
                        } else {
                            // Gérer l'erreur
                            exception?.printStackTrace()
                        }
                    }
                } else {
                    // Auth failed
                    ex?.printStackTrace()
                }
            } else {
                // Log une erreur appropriée si data est null
                println("Data intent is null")
            }
        }
    }



    companion object {
        const val RC_AUTH = 100
    }
}

@Composable
fun MainScreen(context: MainActivity, navController: NavHostController) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHostContainer(navController = navController, modifier = Modifier.padding(innerPadding), context = context)
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Events,
        BottomNavItem.Help,
        BottomNavItem.Account
    )
    BottomNavigation {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            val selected = currentRoute == item.route
            val tint = if (selected) Color.White else Color.Gray
            BottomNavigationItem(
                icon = { Icon(painter = painterResource(id = item.icon), contentDescription = item.title, tint = tint) },
                label = { Text(text = item.title, color = tint) },
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun NavHostContainer(navController: NavHostController, modifier: Modifier = Modifier, context: MainActivity) {
    NavHost(navController, startDestination = BottomNavItem.Home.route, modifier = modifier) {
        composable(BottomNavItem.Home.route) { HomeScreen() }
        composable(BottomNavItem.Events.route) { EventsScreen() }
        composable(BottomNavItem.Help.route) { HelpScreen() }
        composable(BottomNavItem.Account.route) {
            val accountViewModel: AccountViewModel = viewModel()
            AccountScreen(viewModel = accountViewModel, context = context)
        }
        composable("account/{token}") { backStackEntry ->
            val token = backStackEntry.arguments?.getString("token")
            AccountInfoScreen(
                token = token,
                onDeleteAccountClick = { /* Ajouter la logique pour supprimer le compte */ },
                onEditInfoClick = { /* Ajouter la logique pour éditer les infos */ },
                onLogoutClick = {
                    /* Ajouter la logique pour déconnecter */
                    // Exemple :
                    navController.navigate(BottomNavItem.Home.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                },
                onViewEventsClick = { /* Ajouter la logique pour visualiser les événements */ }
            )
        }
    }
}

sealed class BottomNavItem(var title: String, var icon: Int, var route: String) {
    object Home : BottomNavItem("Home", R.drawable.baseline_home_24, "home")
    object Events : BottomNavItem("Events", R.drawable.baseline_directions_run_24, "events")
    object Help : BottomNavItem("Help", R.drawable.baseline_email_24, "help")
    object Account : BottomNavItem("Account", R.drawable.baseline_person_24, "account")
}

@Preview
@Composable
fun MainScreenPreview() {
    MainScreen(context = MainActivity(), navController = rememberNavController())
}
