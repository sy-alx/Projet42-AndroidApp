package com.example.projet42_androidapp.screens

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.view.MotionEvent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.projet42_androidapp.viewmodel.AccountViewModel
import com.example.projet42_androidapp.viewmodel.EventDetailViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import org.json.JSONObject
import com.example.projet42_androidapp.R
import org.osmdroid.events.DelayedMapListener
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import java.io.File

@SuppressLint("ClickableViewAccessibility")
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EventDetailsScreen(eventId: Long, navController: NavController, accountViewModel: AccountViewModel) {
    val viewModel: EventDetailViewModel = viewModel()
    val eventDetails by viewModel.eventDetails.collectAsState()
    val context = LocalContext.current

    var isRegistered by remember { mutableStateOf(false) }
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var showFileDialog by remember { mutableStateOf(false) }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var isEditing by remember { mutableStateOf(false) }

    val pickPdfLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        selectedFileUri = uri
    }

    LaunchedEffect(eventId) {
        viewModel.fetchEventDetails(eventId)
        if (accountViewModel.isUserLoggedIn.value) {
            accountViewModel.isRegisteredToEvent(eventId) { result ->
                isRegistered = result
            }
        }
    }

    DisposableEffect(Unit) {
        val config = Configuration.getInstance()
        config.load(context, context.getSharedPreferences("osmdroid", 0))
        onDispose { }
    }

    var isMapTouched by remember { mutableStateOf(false) }
    var showDateDialog by remember { mutableStateOf(false) }
    var showTimeDialog by remember { mutableStateOf(false) }
    var showStatusDialog by remember { mutableStateOf(false) }
    var editDate by remember { mutableStateOf("") }
    var editTime by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf(1L) }


    LaunchedEffect(isEditing) {
        if (isEditing) {
            editDate = eventDetails?.eventDate ?: ""
            editTime = eventDetails?.eventTime ?: ""
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
        eventDetails?.let { details ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState(), enabled = !isMapTouched),
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

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .padding(8.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            var mapView by remember { mutableStateOf<MapView?>(null) }

                            AndroidView(
                                factory = { ctx ->
                                    MapView(ctx).apply {
                                        setTileSource(TileSourceFactory.MAPNIK)
                                        setMultiTouchControls(true)

                                        val mapController = controller
                                        mapController.setZoom(15.0)

                                        val jsonObject = JSONObject(details.route)
                                        val features = jsonObject.getJSONArray("features")

                                        val geoPoints = ArrayList<GeoPoint>()

                                        for (i in 0 until features.length()) {
                                            val feature = features.getJSONObject(i)
                                            val geometry = feature.getJSONObject("geometry")
                                            val geometryType = geometry.getString("type")

                                            when (geometryType) {
                                                "LineString" -> {
                                                    val coordinates = geometry.getJSONArray("coordinates")
                                                    for (j in 0 until coordinates.length()) {
                                                        val coord = coordinates.getJSONArray(j)
                                                        geoPoints.add(GeoPoint(coord.getDouble(1), coord.getDouble(0)))
                                                    }
                                                    val polyline = Polyline().apply {
                                                        setPoints(geoPoints)
                                                        outlinePaint.color = android.graphics.Color.RED
                                                        outlinePaint.strokeWidth = 5f
                                                    }
                                                    overlays.add(polyline)
                                                    if (geoPoints.isNotEmpty()) {
                                                        mapController.setCenter(geoPoints[0])
                                                    }
                                                }
                                                "Point" -> {
                                                    val coordinates = geometry.getJSONArray("coordinates")
                                                    val geoPoint = GeoPoint(coordinates.getDouble(1), coordinates.getDouble(0))
                                                    val marker = org.osmdroid.views.overlay.Marker(this).apply {
                                                        position = geoPoint
                                                        setAnchor(org.osmdroid.views.overlay.Marker.ANCHOR_CENTER, org.osmdroid.views.overlay.Marker.ANCHOR_BOTTOM)
                                                        icon = ctx.getDrawable(R.drawable.ic_marker)
                                                    }
                                                    overlays.add(marker)
                                                    mapController.setCenter(geoPoint)
                                                }
                                                else -> {

                                                }
                                            }
                                        }

                                        val locationOverlay = MyLocationNewOverlay(this)
                                        locationOverlay.enableMyLocation()
                                        overlays.add(locationOverlay)

                                        mapView = this

                                        var isDragging = false

                                        val mapListener = object : MapListener {
                                            override fun onScroll(event: ScrollEvent?): Boolean {
                                                isDragging = true
                                                return true
                                            }

                                            override fun onZoom(event: ZoomEvent?): Boolean {
                                                return false
                                            }
                                        }

                                        this.setMapListener(DelayedMapListener(mapListener))

                                        this.setOnTouchListener { v, event ->
                                            if (event.action == MotionEvent.ACTION_UP) {
                                                if (isDragging) {
                                                    isDragging = false
                                                    isMapTouched = false
                                                }
                                            } else if (event.action == MotionEvent.ACTION_DOWN) {
                                                isMapTouched = true
                                            }
                                            false
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .pointerInteropFilter { event ->
                                        when (event.action) {
                                            MotionEvent.ACTION_DOWN -> {
                                                isMapTouched = true
                                            }
                                            MotionEvent.ACTION_UP -> {
                                                isMapTouched = false
                                            }
                                        }
                                        mapView?.onTouchEvent(event) ?: false
                                    }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Date :", color = Color.Black)
                        Text(
                            text = if (isEditing) editDate else details.eventDate,
                            color = Color.Gray,
                            modifier = Modifier
                                .background(
                                    color = if (isEditing) Color(0xFFe2a7ec) else Color(0xFFF0F0F0),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(8.dp)
                                .clickable(enabled = isEditing) {
                                    showDateDialog = true
                                    editDate = details.eventDate
                                }
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Heure :", color = Color.Black)
                        Text(
                            text = if (isEditing) editTime else details.eventTime,
                            color = Color.Gray,
                            modifier = Modifier
                                .background(
                                    color = if (isEditing) Color(0xFFe2a7ec) else Color(0xFFF0F0F0),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(8.dp)
                                .clickable(enabled = isEditing) {
                                    showTimeDialog = true
                                    editTime = details.eventTime
                                }
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Statut :", color = Color.Black)
                        Text(
                            text = details.status,
                            color = Color.Gray,
                            modifier = Modifier
                                .background(
                                    color = if (isEditing) Color(0xFFe2a7ec) else Color(0xFFF0F0F0),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(8.dp)
                                .clickable(enabled = isEditing) { showStatusDialog = true }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (accountViewModel.isUserLoggedIn.value) {

                            val isEventFinishedOrCancelled = details.status == "Terminé" || details.status == "Annulé"
                            Button(
                                onClick = {
                                    if (isRegistered) {
                                        showConfirmationDialog = true
                                    } else {
                                        showFileDialog = true
                                    }
                                },
                                enabled = !isEventFinishedOrCancelled,
                                colors = ButtonDefaults.buttonColors(containerColor = if (isRegistered) Color.Red else Color.Blue),
                                shape = RoundedCornerShape(25.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (isEventFinishedOrCancelled) "Inscription impossible" else if (isRegistered) "Annuler l'inscription" else "S'inscrire à l'évènement",
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            if (accountViewModel.isAdmin.value) {
                                Button(
                                    onClick = { isEditing = !isEditing },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0)),
                                    shape = RoundedCornerShape(25.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(text = if (isEditing) "Annuler" else "Editer", color = Color.White)
                                }
                            }
                        }

                        if (showConfirmationDialog) {
                            AlertDialog(
                                onDismissRequest = { showConfirmationDialog = false },
                                title = { Text("Confirmation") },
                                text = { Text("Êtes-vous sûr de vouloir annuler votre inscription ?") },
                                confirmButton = {
                                    Button(
                                        onClick = {
                                            accountViewModel.unregisterFromEvent(
                                                eventId = eventId,
                                                onSuccess = {
                                                    Log.d("EventDetailsScreen", "Successfully unregistered")
                                                    isRegistered = false
                                                    showConfirmationDialog = false
                                                },
                                                onError = { error ->
                                                    Log.e("EventDetailsScreen", "Error unregistering: $error")
                                                    showConfirmationDialog = false
                                                }
                                            )
                                        }
                                    ) {
                                        Text("Oui")
                                    }
                                },
                                dismissButton = {
                                    Button(
                                        onClick = { showConfirmationDialog = false }
                                    ) {
                                        Text("Non")
                                    }
                                }
                            )
                        }

                        if (showFileDialog) {
                            AlertDialog(
                                onDismissRequest = { showFileDialog = false },
                                title = { Text("Déposer un fichier") },
                                text = {
                                    Column {
                                        Button(
                                            onClick = { pickPdfLauncher.launch("application/pdf") }
                                        ) {
                                            Text("Choisir un fichier PDF")
                                        }
                                        selectedFileUri?.let { uri ->
                                            Text("Fichier sélectionné : ${uri.path}")
                                        }
                                    }
                                },
                                confirmButton = {
                                    Button(
                                        onClick = {
                                            selectedFileUri?.let { uri ->
                                                val fileName = "${accountViewModel.userLastName.value}_${accountViewModel.userFirstName.value}_$eventId.pdf"
                                                val inputStream = context.contentResolver.openInputStream(uri)
                                                val file = File(context.cacheDir, fileName)
                                                inputStream?.use { input ->
                                                    file.outputStream().use { output ->
                                                        input.copyTo(output)
                                                    }
                                                }
                                                accountViewModel.registerToEvent(
                                                    eventId = eventId,
                                                    file = file,
                                                    onSuccess = {
                                                        Log.d("EventDetailsScreen", "Successfully registered")
                                                        isRegistered = true
                                                        showFileDialog = false
                                                    },
                                                    onError = { error ->
                                                        Log.e("EventDetailsScreen", "Error registering: $error")
                                                        showFileDialog = false
                                                    }
                                                )
                                            }
                                        }
                                    ) {
                                        Text("Valider l'inscription")
                                    }
                                },
                                dismissButton = {
                                    Button(
                                        onClick = { showFileDialog = false }
                                    ) {
                                        Text("Annuler")
                                    }
                                }
                            )
                        }

                        if (showDateDialog) {
                            AlertDialog(
                                onDismissRequest = { showDateDialog = false },
                                title = { Text("Modifier la Date") },
                                text = {
                                    Column {
                                        OutlinedTextField(
                                            value = editDate,
                                            onValueChange = { newValue -> editDate = newValue },
                                            label = { Text("Date") },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                },
                                confirmButton = {
                                    Button(
                                        onClick = {
                                            accountViewModel.getAuthToken()?.let { token ->
                                                viewModel.updateEventDate(eventId, editDate, token, {
                                                    showDateDialog = false
                                                    viewModel.fetchEventDetails(eventId)
                                                }, { error ->
                                                    Log.e("EventDetailsScreen", "Error updating date: $error")
                                                })
                                            }
                                        }
                                    ) {
                                        Text("Enregistrer")
                                    }
                                },
                                dismissButton = {
                                    Button(
                                        onClick = { showDateDialog = false }
                                    ) {
                                        Text("Annuler")
                                    }
                                }
                            )
                        }

                        if (showTimeDialog) {
                            AlertDialog(
                                onDismissRequest = { showTimeDialog = false },
                                title = { Text("Modifier l'Heure") },
                                text = {
                                    Column {
                                        OutlinedTextField(
                                            value = editTime,
                                            onValueChange = { newValue -> editTime = newValue },
                                            label = { Text("Heure") },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                },
                                confirmButton = {
                                    Button(
                                        onClick = {
                                            accountViewModel.getAuthToken()?.let { token ->
                                                viewModel.updateEventTime(eventId, editTime, token, {
                                                    showTimeDialog = false
                                                    viewModel.fetchEventDetails(eventId)
                                                }, { error ->
                                                    Log.e("EventDetailsScreen", "Error updating time: $error")
                                                })
                                            }
                                        }
                                    ) {
                                        Text("Enregistrer")
                                    }
                                },
                                dismissButton = {
                                    Button(
                                        onClick = { showTimeDialog = false }
                                    ) {
                                        Text("Annuler")
                                    }
                                }
                            )
                        }

                        if (showStatusDialog) {
                            AlertDialog(
                                onDismissRequest = { showStatusDialog = false },
                                title = { Text("Modifier le Statut") },
                                text = {
                                    Column {
                                        var expanded by remember { mutableStateOf(false) }
                                        Button(onClick = { expanded = true }) {
                                            Text("Sélectionner un statut")
                                        }
                                        DropdownMenu(
                                            expanded = expanded,
                                            onDismissRequest = { expanded = false }
                                        ) {
                                            DropdownMenuItem(
                                                text = { Text("A venir") },
                                                onClick = {
                                                    selectedStatus = 1L
                                                    expanded = false
                                                }
                                            )
                                            DropdownMenuItem(
                                                text = { Text("Annulé") },
                                                onClick = {
                                                    selectedStatus = 2L
                                                    expanded = false
                                                }
                                            )
                                            DropdownMenuItem(
                                                text = { Text("Terminé") },
                                                onClick = {
                                                    selectedStatus = 3L
                                                    expanded = false
                                                }
                                            )
                                        }
                                    }
                                },
                                confirmButton = {
                                    Button(
                                        onClick = {
                                            accountViewModel.getAuthToken()?.let { token ->
                                                viewModel.updateEventStatus(eventId, selectedStatus, token, {
                                                    showStatusDialog = false
                                                    viewModel.fetchEventDetails(eventId)
                                                }, { error ->
                                                    Log.e("EventDetailsScreen", "Error updating status: $error")
                                                })
                                            }
                                        }
                                    ) {
                                        Text("Enregistrer")
                                    }
                                },
                                dismissButton = {
                                    Button(
                                        onClick = { showStatusDialog = false }
                                    ) {
                                        Text("Annuler")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        } ?: run {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}












