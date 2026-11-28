
package com.example.canteengo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.canteengo.ui.theme.CanteenGoTheme

// CanteenGo Peach Café palette
private val PeachBackground = Color(0xFFFFF0DC)
private val Orange = Color(0xFFE87524)
private val DarkOrange = Color(0xFFB95319)
private val BrownText = Color(0xFF60391F)
private val MutedBrown = Color(0xFF8A674F)
private val WhiteCream = Color(0xFFFFFAF4)
private val LightPeach = Color(0xFFFFE2C2)

@Composable
fun SignUpScreen(
    onSignUp: (String, String) -> Unit,
    onLogin: () -> Unit,
    busy: Boolean = false
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PeachBackground)
            .verticalScroll(rememberScrollState()).imePadding()
            .padding(horizontal = 25.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(80.dp))

        // HEADER
        Text(
            text = "Customer Sign Up",
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold,
            color = BrownText
        )

        Spacer(modifier = Modifier.height(7.dp))

        Text(
            text = "Fill in your details to get started",
            fontSize = 13.sp,
            color = MutedBrown,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        // FORM CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = WhiteCream
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 3.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // USERNAME
                OutlinedTextField(
                    value = username,
                    onValueChange = {
                        username = it
                        errorMessage = ""
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Username") },
                    placeholder = { Text("Choose a username") },
                    singleLine = true,
                    shape = RoundedCornerShape(13.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Orange,
                        unfocusedBorderColor = LightPeach,
                        focusedLabelColor = Orange,
                        cursorColor = Orange,
                        focusedContainerColor = WhiteCream,
                        unfocusedContainerColor = WhiteCream
                    )
                )

                Spacer(modifier = Modifier.height(13.dp))

                // PASSWORD
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorMessage = ""
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Password") },
                    placeholder = { Text("Create a password") },
                    singleLine = true,
                    shape = RoundedCornerShape(13.dp),
                    visualTransformation = if (passwordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    trailingIcon = {
                        TextButton(
                            onClick = {
                                passwordVisible = !passwordVisible
                            }
                        ) {
                            Text(
                                text = if (passwordVisible) "Hide" else "Show",
                                color = Orange,
                                fontSize = 12.sp
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Orange,
                        unfocusedBorderColor = LightPeach,
                        focusedLabelColor = Orange,
                        cursorColor = Orange,
                        focusedContainerColor = WhiteCream,
                        unfocusedContainerColor = WhiteCream
                    )
                )

                Spacer(modifier = Modifier.height(13.dp))

                // CONFIRM PASSWORD
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        errorMessage = ""
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Confirm Password") },
                    placeholder = { Text("Confirm your password") },
                    singleLine = true,
                    shape = RoundedCornerShape(13.dp),
                    visualTransformation = if (confirmPasswordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    trailingIcon = {
                        TextButton(
                            onClick = {
                                confirmPasswordVisible =
                                    !confirmPasswordVisible
                            }
                        ) {
                            Text(
                                text = if (confirmPasswordVisible)
                                    "Hide"
                                else
                                    "Show",
                                color = Orange,
                                fontSize = 12.sp
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Orange,
                        unfocusedBorderColor = LightPeach,
                        focusedLabelColor = Orange,
                        cursorColor = Orange,
                        focusedContainerColor = WhiteCream,
                        unfocusedContainerColor = WhiteCream
                    )
                )

                Spacer(modifier = Modifier.height(18.dp))

                // ERROR MESSAGE
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color(0xFFB3261E),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                }

                // SIGN UP BUTTON
                Button(
                    enabled = !busy,
                    onClick = {
                        when {
                            !username.trim().matches(Regex("[a-z0-9][a-z0-9_.-]{2,39}")) -> {
                                errorMessage = "Use 3–40 lowercase letters, numbers, dots, underscores or hyphens."
                            }
                            password != confirmPassword -> {
                                errorMessage =
                                    "Passwords do not match."
                            }

                            else -> {
                                errorMessage = ""

                                onSignUp(
                                    username.trim(),
                                    password
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Orange
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 3.dp
                    )
                ) {
                    Text(
                        text = "Create Account",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // LOGIN LINK
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Already have an account?",
                fontSize = 13.sp,
                color = MutedBrown
            )

            TextButton(
                onClick = onLogin
            ) {
                Text(
                    text = "Customer Login",
                    color = DarkOrange,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Davao del Norte State College",
            fontSize = 11.sp,
            color = MutedBrown,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(25.dp))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SignUpScreenPreview() {
    CanteenGoTheme {
        SignUpScreen(
            onSignUp = { _, _ -> },
            onLogin = {}
        )
    }
}
