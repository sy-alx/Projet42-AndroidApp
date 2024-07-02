package com.example.projet42_androidapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HelpScreen() {
    var expandedIndex by remember { mutableStateOf(-1) }

    val questionsAndAnswers = listOf(
        "Comment m'inscrire à un événement ?" to "Pour vous inscrire à un événement, connectez-vous à votre compte ou créez-en un si vous n'en avez pas encore. Une fois connecté, allez dans l'onglet des événements, sélectionnez l'événement souhaité, et déposez un certificat médical au format PDF en utilisant le bouton prévu à cet effet.",
        "Quand je me désinscris d'un événement, est-ce que mon certificat médical est conservé ?" to "Lors de votre désinscription, le certificat médical que vous avez déposé est supprimé. De plus, lorsque l'événement prend fin, votre certificat médical est conservé temporairement pour la visualisation de l'événement. Lors de la suppression de l'événement, votre certificat est définitivement supprimé.",
        "À quoi sert le QR code disponible depuis l'accueil ?" to "Votre QR code personnel est requis lors des événements. Un organisateur le scannera pour vérifier votre inscription. En cas de non-inscription, nous nous réservons le droit de vous refuser l'accès à l'événement pour des raisons logistiques."
    )


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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Aide & Contact",
                    fontSize = 24.sp,
                    color = Color.White
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Text(
                    text = "Questions récurrentes",
                    fontSize = 20.sp,
                    color = Color.White
                )
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
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
                        questionsAndAnswers.forEachIndexed { index, pair ->
                            val (question, answer) = pair
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        expandedIndex = if (expandedIndex == index) -1 else index
                                    }
                                    .padding(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = question,
                                        fontSize = 16.sp,
                                        color = Color.Black,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Icon(
                                        imageVector = if (expandedIndex == index) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                        tint = Color.Black
                                    )
                                }
                                if (expandedIndex == index) {
                                    Text(
                                        text = answer,
                                        fontSize = 14.sp,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Text(
                    text = "Contact",
                    fontSize = 20.sp,
                    color = Color.White
                )
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Pour toutes autres questions : admin@projet42.fr",
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}
