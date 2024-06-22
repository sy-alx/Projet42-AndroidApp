package com.example.projet42_androidapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.projet42_androidapp.viewmodel.EventDetailViewModel

@Composable
fun EventDetailsScreen(eventId: Long, navController: NavController) {
    val viewModel: EventDetailViewModel = viewModel()
    val eventDetails by viewModel.eventDetails.collectAsState()

    LaunchedEffect(eventId) {
        viewModel.fetchEventDetails(eventId)
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
        eventDetails?.let { details ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()), // Ajout de verticalScroll
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
                        text = "Détails de l'évènement",
                        fontSize = 24.sp,
                        color = Color.White,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
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
                            text = details.name,
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                            color = Color.Black
                        )
                        Text(text = "Liste des sports :", color = Color.Black)
                        details.sports.forEach { sport ->
                            Text(text = "- $sport", color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Description :", color = Color.Black)
                        Text(
                            text = details.description,
                            color = Color.Gray,
                            modifier = Modifier
                                .background(
                                    color = Color(0xFFF0F0F0),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(8.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Adresse :", color = Color.Black)
                        Text(
                            text = "- ${details.address}",
                            color = Color.Gray,
                            modifier = Modifier
                                .background(
                                    color = Color(0xFFF0F0F0),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(8.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Parcours :", color = Color.Black)
                        Text(
                            text = details.route,
                            color = Color.Gray,
                            modifier = Modifier
                                .background(
                                    color = Color(0xFFF0F0F0),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(8.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Date :", color = Color.Black)
                        Text(
                            text = details.eventDate,
                            color = Color.Gray,
                            modifier = Modifier
                                .background(
                                    color = Color(0xFFF0F0F0),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(8.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Heure :", color = Color.Black)
                        Text(
                            text = details.eventTime,
                            color = Color.Gray,
                            modifier = Modifier
                                .background(
                                    color = Color(0xFFF0F0F0),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(8.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Statut :", color = Color.Black)
                        Text(
                            text = details.status,
                            color = Color.Gray,
                            modifier = Modifier
                                .background(
                                    color = Color(0xFFF0F0F0),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(8.dp)
                        )
                    }
                }
            }
        } ?: run {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}
