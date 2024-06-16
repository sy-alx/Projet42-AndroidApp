package com.example.projet42_androidapp.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projet42_androidapp.utils.AuthConfig
import com.example.projet42_androidapp.viewmodel.AccountViewModel

@Composable
fun AccountInfoScreen(
    token: String? = null,
    refreshToken: String? = null,
    viewModel: AccountViewModel = viewModel(),
    onLogoutClick: () -> Unit,
    onEditInfoClick: () -> Unit,
    onViewEventsClick: () -> Unit,
    onDeleteAccountClick: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(token, refreshToken) {
        token?.let {
            viewModel.initializeTokens(token, refreshToken)
            viewModel.fetchUserInfo(it)
        }
    }

    val userFirstName by viewModel.userFirstName
    val userLastName by viewModel.userLastName
    val userEmail by viewModel.userEmail
    val isEditing by viewModel.isEditing

    var showModalEmail by remember { mutableStateOf(false) }
    var newEmail by remember { mutableStateOf("") }

    var showModalPassword by remember { mutableStateOf(false) }
    var newPassword by remember { mutableStateOf("") }

    if (showModalEmail) {
        AlertDialog(
            onDismissRequest = { showModalEmail = false },
            title = { Text("Modifier l'email") },
            text = {
                Column {
                    TextField(
                        value = newEmail,
                        onValueChange = { newEmail = it },
                        label = { Text("Nouvel email") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateEmail(newEmail, context, onLogoutClick)
                        showModalEmail = false
                    }
                ) {
                    Text("Enregistrer")
                }
            },
            dismissButton = {
                Button(onClick = { showModalEmail = false }) {
                    Text("Annuler")
                }
            }
        )
    }

    if (showModalPassword) {
        AlertDialog(
            onDismissRequest = { showModalPassword = false },
            title = { Text("Modifier le mot de passe") },
            text = {
                Column {
                    TextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("Nouveau mot de passe") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updatePassword(newPassword, context, onLogoutClick)
                        showModalPassword = false
                    }
                ) {
                    Text("Enregistrer")
                }
            },
            dismissButton = {
                Button(onClick = { showModalPassword = false }) {
                    Text("Annuler")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF3700B3), Color(0xFF6650a4)),
                    tileMode = TileMode.Clamp
                )
            )
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Bienvenue $userFirstName $userLastName !", fontSize = 24.sp, color = Color.White)

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(Color.White, shape = RoundedCornerShape(25.dp))
                    .padding(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    TextField(
                        value = userFirstName,
                        onValueChange = {},
                        label = { Text("Nom") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null)
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = userLastName,
                        onValueChange = {},
                        label = { Text("Prénom") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null)
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            onEditInfoClick()
                            viewModel.toggleEditMode()
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF9C27B0)),
                        shape = RoundedCornerShape(25.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = if (isEditing) "Annuler" else "Modifier mes informations", color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextField(
                        value = userEmail,
                        onValueChange = {},
                        label = { Text("Email") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null)
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = if (isEditing) Color.LightGray else Color.White,
                            focusedIndicatorColor = if (isEditing) Color.Blue else Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = isEditing) { showModalEmail = true },
                        enabled = false
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = "",
                        onValueChange = {},
                        label = { Text("Mot de passe") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null)
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = if (isEditing) Color.LightGray else Color.White,
                            focusedIndicatorColor = if (isEditing) Color.Blue else Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = isEditing) { showModalPassword = true },
                        enabled = false
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onViewEventsClick,
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF9C27B0)),
                        shape = RoundedCornerShape(25.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Événements notés", color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (token != null && refreshToken != null) {
                                AuthConfig.logout(
                                    context = context,
                                    token = token,
                                    refreshToken = refreshToken,
                                    onSuccess = {
                                        viewModel.isUserLoggedIn.value = false
                                        onLogoutClick()
                                    },
                                    onError = {
                                        Log.e("AccountInfoScreen", "Failed to logout")
                                    }
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF9C27B0)),
                        shape = RoundedCornerShape(25.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Déconnexion", color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onDeleteAccountClick,
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFFA500)),
                        shape = RoundedCornerShape(25.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Supprimer mon compte", color = Color.White)
                    }
                }
            }
        }
    }
}
