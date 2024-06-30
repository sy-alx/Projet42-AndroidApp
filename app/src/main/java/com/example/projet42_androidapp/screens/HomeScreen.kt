package com.example.projet42_androidapp.screens

import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.projet42_androidapp.R
import com.example.projet42_androidapp.viewmodel.HomeViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.projet42_androidapp.viewmodel.AccountViewModel


@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = viewModel(), accountViewModel: AccountViewModel) {
    val nearestEvent by viewModel.nearestEvent.collectAsState()
    val isRegistered by viewModel.isRegistered.collectAsState()
    val isUserLoggedIn by accountViewModel.isUserLoggedIn

    LaunchedEffect(Unit) {
        viewModel.fetchNearestEvent()
    }

    LaunchedEffect(nearestEvent, isUserLoggedIn) {
        nearestEvent?.let {
            if (isUserLoggedIn) {
                viewModel.checkIfUserIsRegistered(it.id, accountViewModel)
            } else {
                viewModel.setRegistered(false)
            }
        }
    }

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
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeaderSection()
            Spacer(modifier = Modifier.height(16.dp))
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        EventImage()
                        Text(
                            text = "Notre prochain évènement",
                            color = Color.White,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center
                        )
                        nearestEvent?.let {
                            EventCard(event = it, onClick = {
                                navController.navigate("eventDetails/${it.id}")
                            }, isRegistered = isRegistered)
                        } ?: run {
                            Text(
                                text = "Aucun évènement à venir",
                                color = Color.White,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { /* TODO: Handle QR Code click */ }) {
            Icon(
                imageVector = Icons.Default.QrCode,
                contentDescription = "QR Code",
                tint = Color.White
            )
        }
        Text(
            text = "Bienvenue sur l'application\nLes amis du sport",
            color = Color.White,
            fontSize = 20.sp,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        IconButton(onClick = { /* TODO: Handle settings click */ }) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = Color.White
            )
        }
    }
}

@Composable
fun EventImage() {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.event_image),
            contentDescription = "Event Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun EventCard(event: HomeViewModel.EventSummary, onClick: () -> Unit, isRegistered: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = event.name,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Adresse: ${event.address}", color = Color.Gray)
            Text(text = "Status: ${event.status}", color = Color.Gray)
            Text(text = "Date: ${event.eventDate}", color = Color.Gray)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Heure: ${event.eventTime}", color = Color.Gray, modifier = Modifier.weight(1f))
                Icon(Icons.Filled.ArrowForward, contentDescription = "Arrow Forward", tint = Color.Gray)
                if (isRegistered) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Filled.CheckCircle, contentDescription = "Registered", tint = Color.Green)
                }
            }
        }
    }
}