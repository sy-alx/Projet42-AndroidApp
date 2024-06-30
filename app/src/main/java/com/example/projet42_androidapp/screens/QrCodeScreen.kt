package com.example.projet42_androidapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.projet42_androidapp.viewmodel.AccountViewModel
import com.example.projet42_androidapp.viewmodel.QrCodeViewModel

@Composable
fun QrCodeScreen(
    navController: NavController,
    accountViewModel: AccountViewModel,
    qrCodeViewModel: QrCodeViewModel = viewModel()
) {
    val isUserLoggedIn by accountViewModel.isUserLoggedIn
    val userSub by accountViewModel.userSub

    if (!isUserLoggedIn) {
        // Show an error message if the user is not logged in
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF3700B3), Color(0xFF6650a4)),
                        tileMode = TileMode.Clamp
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Vous devez être connecté pour accéder à cette page",
                    fontSize = 24.sp,
                    color = Color.White,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Button(onClick = { navController.popBackStack() }) {
                    Text(text = "Revenir à la page précédente")
                }
            }
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF3700B3), Color(0xFF6650a4)),
                        tileMode = TileMode.Clamp
                    )
                )
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "QR Code",
                        fontSize = 24.sp,
                        color = Color.White,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                userSub?.let { sub ->
                    val qrCodeBitmap = qrCodeViewModel.generateQRCode(sub, 512)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(bitmap = qrCodeBitmap.asImageBitmap(), contentDescription = "QR Code")
                    }
                }
            }
        }
    }
}
