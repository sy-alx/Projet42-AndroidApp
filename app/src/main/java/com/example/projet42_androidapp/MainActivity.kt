package com.example.projet42_androidapp

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import com.example.projet42_androidapp.Activity.QRCodeScannerActivity
import com.example.projet42_androidapp.screens.AccountInfoScreen
import com.example.projet42_androidapp.screens.AccountScreen
import com.example.projet42_androidapp.screens.EventDetailsScreen
import com.example.projet42_androidapp.screens.EventsScreen
import com.example.projet42_androidapp.screens.HelpScreen
import com.example.projet42_androidapp.screens.HomeScreen
import com.example.projet42_androidapp.screens.LoginScreen
import com.example.projet42_androidapp.screens.QrCodeScreen
import com.example.projet42_androidapp.screens.RegisterScreen
import com.example.projet42_androidapp.ui.theme.Projet42AndroidAppTheme
import com.example.projet42_androidapp.utils.AuthConfig
import com.example.projet42_androidapp.utils.CustomConnectionBuilder
import com.example.projet42_androidapp.viewmodel.AccountViewModel
import net.openid.appauth.AppAuthConfiguration
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService

@Suppress("DEPRECATION")
class MainActivity : ComponentActivity() {
    private lateinit var authService: AuthorizationService
    private lateinit var navController: NavHostController

    private val accountViewModel: AccountViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                    MainScreen(context = this, navController = navController, accountViewModel = accountViewModel)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission granted, launch the scanner
                val intent = Intent(this, QRCodeScannerActivity::class.java)
                startActivityForResult(intent, QRCodeScannerActivity.REQUEST_CODE_SCAN)
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(this, "Camera permission is required to scan QR codes", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Inside your MainActivity or wherever you are using VerifyTokenTask
    private fun startAuthorization() {
        val authRequest = AuthConfig.createAuthRequest(this)
        val authService = AuthConfig.createAuthService(this)

        val intent = authService.getAuthorizationRequestIntent(authRequest)
        startActivityForResult(intent, RC_AUTH)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_AUTH) {
            val resp = AuthorizationResponse.fromIntent(data!!)
            val ex = AuthorizationException.fromIntent(data)
            if (resp != null) {
                val clientAuth = AuthConfig.createClientAuthentication()
                authService.performTokenRequest(
                    resp.createTokenExchangeRequest(),
                    clientAuth
                ) { tokenResponse, exception ->
                    if (tokenResponse != null) {
                        val accessToken = tokenResponse.accessToken
                        val refreshToken = tokenResponse.refreshToken
                        Log.d("AuthToken", "Access Token: $accessToken")

                        // Verify the token signature
                        accessToken?.let { token ->
                            VerifyTokenTask(token) { isTokenValid, validatedToken ->
                                if (isTokenValid) {
                                    Log.d("AuthToken", "Token is valid: $validatedToken")
                                    accountViewModel.initializeTokens(accessToken, refreshToken)
                                    navController.navigate("account/${accessToken}/${refreshToken}")
                                } else {
                                    Log.e("AuthToken", "Invalid token signature")
                                }
                            }.execute()
                        }
                    } else {
                        exception?.printStackTrace()
                    }
                }
            } else {
                ex?.printStackTrace()
            }
        }
    }

    companion object {
        const val RC_AUTH = 100
    }
}


@Composable
fun MainScreen(context: MainActivity, navController: NavHostController, accountViewModel: AccountViewModel) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHostContainer(navController = navController, modifier = Modifier.padding(innerPadding), context = context, accountViewModel = accountViewModel)
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
fun NavHostContainer(navController: NavHostController, modifier: Modifier = Modifier, context: MainActivity, accountViewModel: AccountViewModel) {
    NavHost(navController, startDestination = BottomNavItem.Home.route, modifier = modifier) {
        composable(BottomNavItem.Home.route) { HomeScreen(navController = navController, accountViewModel = accountViewModel) }
        composable(BottomNavItem.Events.route) { EventsScreen(navController = navController, accountViewModel = accountViewModel) }
        composable(BottomNavItem.Help.route) { HelpScreen() }
        composable(BottomNavItem.Account.route) {
            AccountScreen(viewModel = accountViewModel, context = context)
        }
        composable("login") {
            LoginScreen(context = context, onLoginSuccess = { token ->
                navController.navigate("account/$token")
            }, onRegisterClick = {
                navController.navigate("register")
            })
        }
        composable("register") {
            RegisterScreen(
                onRegisterClick = {
                    navController.navigate("login")
                },
                onLoginClick = {
                    navController.navigate("login")
                }
            )
        }
        composable("account/{token}/{refreshToken}") { backStackEntry ->
            val token = backStackEntry.arguments?.getString("token")
            val refreshToken = backStackEntry.arguments?.getString("refreshToken")
            AccountInfoScreen(
                token = token,
                refreshToken = refreshToken,
                viewModel = accountViewModel,
                onDeleteAccountClick = { /* Ajouter la logique pour supprimer le compte */ },
                onEditInfoClick = { /* Ajouter la logique pour éditer les infos */ },
                onLogoutClick = {
                    navController.navigate(BottomNavItem.Home.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                },
                onViewEventsClick = { /* Ajouter la logique pour visualiser les événements */ }
            )
        }
        composable("eventDetails/{eventId}") { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")
            EventDetailsScreen(eventId = eventId?.toLong() ?: 0, navController = navController, accountViewModel = accountViewModel)
        }
        composable("qrCode") {
            QrCodeScreen(navController = navController, accountViewModel = accountViewModel)
        }
    }
}



sealed class BottomNavItem(var title: String, var icon: Int, var route: String) {
    object Home : BottomNavItem("Home", R.drawable.baseline_home_24, "home")
    object Events : BottomNavItem("Events", R.drawable.baseline_directions_run_24, "events")
    object Help : BottomNavItem("Help", R.drawable.baseline_email_24, "help")
    object Account : BottomNavItem("Account", R.drawable.baseline_person_24, "account")
}

