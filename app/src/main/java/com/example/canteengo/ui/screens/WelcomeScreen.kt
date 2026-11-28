
package com.example.canteengo.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.canteengo.R
import com.example.canteengo.ui.theme.CanteenGoTheme

// Pinterest-inspired Peach Café palette
private val PeachBackground = Color(0xFFFFF0DC)
private val LightPeach = Color(0xFFFFE2C2)
private val Orange = Color(0xFFE87524)
private val DarkOrange = Color(0xFFB95319)
private val BrownText = Color(0xFF60391F)
private val MutedBrown = Color(0xFF8A674F)
private val WhiteCream = Color(0xFFFFFAF4)

@Composable
fun WelcomeScreen(
    onStudent: () -> Unit,
    onStaff: () -> Unit,
    onConnect: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PeachBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(50.dp))

        // LOGO
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xFFFFF0DC),
                    shape = RoundedCornerShape(28.dp)
                )
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.canteengo_logo),
                contentDescription = "CanteenGo Logo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // WELCOME TEXT
        Text(
            text = "Craving something?",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = BrownText,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = "Good food is a tap away.",
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium,
            color = DarkOrange,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        // DESCRIPTION CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = PeachBackground
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Your campus. Your cravings. One tap.",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Orange,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Pick your favorites. Skip the queue.\nOrder ahead and enjoy a hassle-free meal.",
                    fontSize = 13.sp,
                    color = MutedBrown,
                    textAlign = TextAlign.Center,
                    lineHeight = 21.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // STUDENT BUTTON
        Button(
            onClick = onStudent,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(17.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Orange
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 3.dp
            )
        ) {
            Text(
                text = "Log In",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(13.dp))

        // CONNECT TO SERVER
        OutlinedButton(
            onClick = onConnect,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(17.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.5.dp,
                Orange
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = WhiteCream,
                contentColor = DarkOrange
            )
        ) {
            Text(
                text = "Explore local demo",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // STAFF / ADMIN
        TextButton(
            onClick = onStaff
        ) {
            Text(
                text = "Staff / Admin Login",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkOrange
            )
        }

        Spacer(modifier = Modifier.height(26.dp))

        // FOOTER
        HorizontalDivider(
            color = Orange.copy(alpha = 0.25f),
            thickness = 1.dp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "CAMPUS PRE-ORDER • CASH ON PICKUP",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp,
            color = DarkOrange,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(7.dp))

        Text(
            text = "Davao del Norte State College",
            fontSize = 12.sp,
            color = MutedBrown,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(25.dp))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun WelcomeScreenPreview() {
    CanteenGoTheme {
        WelcomeScreen(
            onStudent = {},
            onStaff = {},
            onConnect = {}
        )
    }
}
