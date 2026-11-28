
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import com.example.canteengo.R
import com.example.canteengo.data.LoginPortal
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
fun LoginScreen(
    onLogin: (String, String) -> Unit,
    onGuest: () -> Unit,
    onSignUp: () -> Unit,
    onForgotPassword: (() -> Unit)? = null,
    busy: Boolean = false,
    portal: LoginPortal = LoginPortal.CUSTOMER
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PeachBackground)
            .verticalScroll(rememberScrollState()).imePadding()
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(28.dp))

        // LOGO
        Image(
            painter = painterResource(id = R.drawable.canteengo_logo),
            contentDescription = "CanteenGo Logo",
            modifier = Modifier.size(150.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // HEADER
        Text(
            text = portal.title,
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold,
            color = BrownText
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = if (portal == LoginPortal.ADMIN) "For administrators and canteen staff" else "Sign in to order your campus favorites",
            fontSize = 14.sp,
            color = MutedBrown
        )

        Spacer(modifier = Modifier.height(30.dp))

        // LOGIN CARD
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
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // USERNAME
                OutlinedTextField(
                    value = username,
                    onValueChange = {
                        username = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Username") },
                    placeholder = { Text("Enter your username") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Orange,
                        unfocusedBorderColor = LightPeach,
                        focusedLabelColor = Orange,
                        cursorColor = Orange,
                        focusedContainerColor = WhiteCream,
                        unfocusedContainerColor = WhiteCream
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // PASSWORD
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Password") },
                    placeholder = { Text("Enter your password") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
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

                // Show recovery only when a real recovery action is provided.
                if (onForgotPassword != null) Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onForgotPassword
                    ) {
                        Text(
                            text = "Forgot password?",
                            color = DarkOrange,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // LOGIN BUTTON
                Button(
                    enabled = !busy,
                    onClick = { onLogin(username.trim(), password) },
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
                        text = portal.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                // DIVIDER
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = LightPeach
                    )

                    Text(
                        text = "  or  ",
                        fontSize = 12.sp,
                        color = MutedBrown
                    )

                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = LightPeach
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // GUEST BUTTON
                OutlinedButton(
                    onClick = onGuest,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = BrownText,
                        containerColor = WhiteCream
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        LightPeach
                    )
                ) {
                    Text(
                        text = "Back to welcome",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Customer registration only; privileged accounts are provisioned separately.
        if (portal == LoginPortal.CUSTOMER) Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Don't have an account?",
                fontSize = 13.sp,
                color = MutedBrown
            )

            TextButton(
                onClick = onSignUp
            ) {
                Text(
                    text = "Sign up",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkOrange
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Davao del Norte State College",
            fontSize = 11.sp,
            color = MutedBrown,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Preview(name = "Admin login", showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun AdminLoginScreenPreview() {
    CanteenGoTheme {
        LoginScreen(onLogin = { _, _ -> }, onGuest = {}, onSignUp = {}, portal = LoginPortal.ADMIN)
    }
}

@Preview(name = "Customer login", showBackground = true, showSystemUi = true)
@Composable
private fun LoginScreenPreview() {
    CanteenGoTheme {
        LoginScreen(
            onLogin = { _, _ -> },
            onGuest = {},
            onSignUp = {},
            onForgotPassword = {}
        )
    }
}
