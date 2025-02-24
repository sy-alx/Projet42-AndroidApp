package com.example.projet42_androidapp.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.projet42_androidapp.MainActivity
import com.example.projet42_androidapp.utils.AuthConfig

@Composable
fun LoginScreen(context: Context, onLoginSuccess: (String) -> Unit, onRegisterClick: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val authService = remember { AuthConfig.createAuthService(context) }
    val authRequest = remember { AuthConfig.createAuthRequest(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF6650a4), Color(0xFF3700B3)),
                    tileMode = TileMode.Clamp
                )
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Vous revoilà !", fontSize = 24.sp, color = Color.White)

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(Color.White, shape = RoundedCornerShape(25.dp))
                    .padding(16.dp)
            ) {
                Column {

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            val authIntent = authService.getAuthorizationRequestIntent(authRequest)
                            (context as MainActivity).startActivityForResult(authIntent, MainActivity.RC_AUTH)
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF9C27B0)),
                        shape = RoundedCornerShape(25.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Connexion", color = Color.White)
                    }

                    Text(text = "Mot de passe oublié ?", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.align(Alignment.CenterHorizontally))


                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = onRegisterClick,
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFFA500)),
                        shape = RoundedCornerShape(25.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Inscription", color = Color.White)
                    }

                    Text(text = "Toujours pas de compte ?", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.align(Alignment.CenterHorizontally))

                }
            }
        }
    }
}

