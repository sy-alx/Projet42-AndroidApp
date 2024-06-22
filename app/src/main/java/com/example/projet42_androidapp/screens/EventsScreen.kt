package com.example.projet42_androidapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.projet42_androidapp.viewmodels.EventSummaryViewModel

@Composable
fun EventsScreen(viewModel: EventSummaryViewModel = viewModel(), navController: NavController) {
    val events by viewModel.events.collectAsState()

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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Nos évènements !",
                fontSize = 24.sp,
                color = Color.White,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(events) { event ->
                    EventCard(event, onClick = { navController.navigate("eventDetails/${event.id}") })
                }
            }
        }
    }
}

@Composable
fun EventCard(event: EventSummaryViewModel.EventSummary, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
            }
        }
    }
}
