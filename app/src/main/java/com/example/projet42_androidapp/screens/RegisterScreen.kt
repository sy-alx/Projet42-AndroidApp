package com.example.projet42_androidapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projet42_androidapp.R
import com.example.projet42_androidapp.viewmodel.AccountViewModel

@Composable
fun RegisterScreen(onRegisterClick: () -> Unit, onLoginClick: () -> Unit, accountViewModel: AccountViewModel = viewModel()) {
    var username by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

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
        Text(text = "Heureux de vous rencontrer !", fontSize = 24.sp, color = Color.White)

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier.padding(16.dp) // simulate margin
        ) {
            Box(
                modifier = Modifier
                    .background(Color.White, shape = RoundedCornerShape(25.dp))
                    .padding(16.dp)
            ) {
                Column {
                    TextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Nom d'utilisateur") },
                        leadingIcon = {
                            Icon(painter = painterResource(id = R.drawable.baseline_person_24), contentDescription = null)
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        label = { Text("Nom") },
                        leadingIcon = {
                            Icon(painter = painterResource(id = R.drawable.baseline_person_24), contentDescription = null)
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        label = { Text("Prénom") },
                        leadingIcon = {
                            Icon(painter = painterResource(id = R.drawable.baseline_person_24), contentDescription = null)
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        leadingIcon = {
                            Icon(painter = painterResource(id = R.drawable.baseline_email_24), contentDescription = null)
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Mot de passe") },
                        leadingIcon = {
                            Icon(painter = painterResource(id = R.drawable.baseline_key_24), contentDescription = null)
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            accountViewModel.register(username, firstName, lastName, email, password, {
                                onLoginClick()  // Redirige vers l'écran de connexion après une inscription réussie
                            }, {
                                errorMessage = it
                            })
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF9C27B0)),
                        shape = RoundedCornerShape(25.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Inscription", color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    errorMessage?.let {
                        Text(text = it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                    }

                    Text(text = "Déjà un compte ?", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.align(Alignment.CenterHorizontally))

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = onLoginClick,
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFFA500)),
                        shape = RoundedCornerShape(25.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Connexion", color = Color.White)
                    }
                }
            }
        }
    }
}
