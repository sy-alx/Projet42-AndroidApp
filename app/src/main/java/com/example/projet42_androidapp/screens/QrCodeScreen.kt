package com.example.projet42_androidapp.screens

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.projet42_androidapp.Activity.QRCodeScannerActivity
import com.example.projet42_androidapp.viewmodel.AccountViewModel
import com.example.projet42_androidapp.viewmodel.QrCodeViewModel
import com.example.projet42_androidapp.viewmodels.EventSummaryViewModel
import com.google.zxing.integration.android.IntentIntegrator
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun QrCodeScreen(
    navController: NavController,
    accountViewModel: AccountViewModel,
    qrCodeViewModel: QrCodeViewModel = viewModel(),
    eventSummaryViewModel: EventSummaryViewModel = viewModel()
) {
    val isUserLoggedIn by accountViewModel.isUserLoggedIn
    val userSub by accountViewModel.userSub
    val isAdmin by accountViewModel.isAdmin
    var showEventListDialog by remember { mutableStateOf(false) }
    var selectedEvent by remember { mutableStateOf<EventSummaryViewModel.EventSummary?>(null) }
    var showCameraDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var popupMessage by remember { mutableStateOf<Pair<Boolean, Boolean>?>(null) } // Pair<showPopup, isSuccess>

    val scannerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val scanResult = result.data?.getStringExtra("SCAN_RESULT")
            scanResult?.let { qrCodeContent ->
                selectedEvent?.let { event ->
                    accountViewModel.getAuthToken()?.let { token ->
                        qrCodeViewModel.checkUserRegistration(event.id, qrCodeContent, token) { isRegistered ->
                            popupMessage = Pair(true, isRegistered)
                        }
                    }
                }
            }
        }
    }

    popupMessage?.let {
        ShowPopupMessage(
            isSuccess = it.second,
            onDismiss = { popupMessage = null }
        )
    }

    if (!isUserLoggedIn) {
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
                    val qrCodeSize = 356.dp
                    val qrCodeBitmap = qrCodeViewModel.generateQRCode(sub, qrCodeSize.value.toInt())

                    Box(
                        modifier = Modifier
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            bitmap = qrCodeBitmap.asImageBitmap(),
                            contentDescription = "QR Code",
                            modifier = Modifier.size(qrCodeSize)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isAdmin) {
                    Button(onClick = { showEventListDialog = true }) {
                        Text(text = "Afficher les événements à venir")
                    }

                    selectedEvent?.let { event ->
                        Text(
                            text = "Événement sélectionné : ${event.name}",
                            fontSize = 14.sp,
                            color = Color.White,
                        )

                        Button(onClick = {
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED
                            ) {
                                ActivityCompat.requestPermissions(
                                    context as ComponentActivity,
                                    arrayOf(Manifest.permission.CAMERA),
                                    0
                                )
                            } else {
                                val intent = Intent(context, QRCodeScannerActivity::class.java)
                                scannerLauncher.launch(intent)
                            }
                        }) {
                            Text(text = "Scanner les QR codes")
                        }
                    }
                }
            }

            if (showEventListDialog) {
                EventListDialog(
                    events = eventSummaryViewModel.events.collectAsState().value,
                    onDismiss = { showEventListDialog = false },
                    onEventSelected = { event ->
                        selectedEvent = event
                        showEventListDialog = false
                    }
                )
            }
        }
    }
}

@Composable
fun EventListDialog(
    events: List<EventSummaryViewModel.EventSummary>,
    onDismiss: () -> Unit,
    onEventSelected: (EventSummaryViewModel.EventSummary) -> Unit
) {
    val sortedEvents = events.sortedBy { it.eventDate }
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Évènements à venir") },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 0.dp, max = 400.dp)
            ) {
                LazyColumn {
                    items(sortedEvents) { event ->
                        EventCard(event = event, onClick = { onEventSelected(event) })
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onDismiss() }) {
                Text(text = "Fermer")
            }
        }
    )
}

@Composable
fun EventCard(event: EventSummaryViewModel.EventSummary, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        backgroundColor = Color.White,
        elevation = 4.dp
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
                style = androidx.compose.material.MaterialTheme.typography.h6,
                color = Color.Black
            )
            Text(text = "Date: ${event.eventDate}", color = Color.Gray)
            Text(text = "Heure: ${event.eventTime}", color = Color.Gray)
        }
    }
}


@Composable
fun ShowPopupMessage(
    isSuccess: Boolean,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Inscription :") },
        text = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (isSuccess) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = Color.Green,
                        modifier = Modifier.size(64.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = "Error",
                        tint = Color.Red,
                        modifier = Modifier.size(64.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = { onDismiss() }) {
                Text("OK")
            }
        }
    )
}
