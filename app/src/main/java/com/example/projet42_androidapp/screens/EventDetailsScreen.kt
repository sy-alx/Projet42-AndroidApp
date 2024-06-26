package com.example.projet42_androidapp.screens

import android.annotation.SuppressLint
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
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
import org.osmdroid.events.DelayedMapListener
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent

@SuppressLint("ClickableViewAccessibility")
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EventDetailsScreen(eventId: Long, navController: NavController, accountViewModel: AccountViewModel) {
    val viewModel: EventDetailViewModel = viewModel()
    val eventDetails by viewModel.eventDetails.collectAsState()
    val context = LocalContext.current

    var isRegistered by remember { mutableStateOf(false) }

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
                                        val coordinates = jsonObject
                                            .getJSONArray("features")
                                            .getJSONObject(0)
                                            .getJSONObject("geometry")
                                            .getJSONArray("coordinates")

                                        val geoPoints = ArrayList<GeoPoint>()
                                        for (i in 0 until coordinates.length()) {
                                            val coord = coordinates.getJSONArray(i)
                                            geoPoints.add(GeoPoint(coord.getDouble(1), coord.getDouble(0)))
                                        }

                                        val polyline = Polyline().apply {
                                            setPoints(geoPoints)
                                            outlinePaint.color = android.graphics.Color.RED
                                            outlinePaint.strokeWidth = 5f
                                        }

                                        overlays.add(polyline)
                                        val locationOverlay = MyLocationNewOverlay(this)
                                        locationOverlay.enableMyLocation()
                                        overlays.add(locationOverlay)

                                        if (geoPoints.isNotEmpty()) {
                                            mapController.setCenter(geoPoints[0])
                                        }

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
                        if (accountViewModel.isUserLoggedIn.value) {
                            Button(
                                onClick = {
                                    if (isRegistered) {
                                        accountViewModel.unregisterFromEvent(eventId,
                                            onSuccess = {
                                                // Handle success, e.g., show a success message or navigate away
                                                Log.d("EventDetailsScreen", "Successfully unregistered")
                                                isRegistered = false
                                            },
                                            onError = { error ->
                                                // Handle error, e.g., show an error message
                                                Log.e("EventDetailsScreen", "Error unregistering: $error")
                                            }
                                        )
                                    } else {
                                        accountViewModel.registerToEvent(eventId,
                                            onSuccess = {
                                                // Handle success, e.g., show a success message or navigate away
                                                Log.d("EventDetailsScreen", "Successfully registered")
                                                isRegistered = true
                                            },
                                            onError = { error ->
                                                // Handle error, e.g., show an error message
                                                Log.e("EventDetailsScreen", "Error registering: $error")
                                            }
                                        )
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = if (isRegistered) Color.Red else Color.Blue),
                                shape = RoundedCornerShape(25.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = if (isRegistered) "Annuler l'inscription" else "S'inscrire à l'évènement", color = Color.White)
                            }
                        }
                    }
                }
            }
        } ?: run {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

