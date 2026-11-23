package com.example.canteengo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.canteengo.ui.theme.CanteenGoTheme
import androidx.lifecycle.ViewModelProvider
import com.example.canteengo.ui.CanteenViewModel
import com.example.canteengo.ui.CanteenGoApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val model = ViewModelProvider(this)[CanteenViewModel::class.java]
        setContent {
            CanteenGoTheme {
                CanteenGoApp(model)
            }
        }
    }
}

