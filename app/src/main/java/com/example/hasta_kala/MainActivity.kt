package com.example.hasta_kala

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.hasta_kala.ui.MainApp
import com.example.hasta_kala.ui.theme.HastaKalaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HastaKalaTheme {
                MainApp()
            }
        }
    }
}
