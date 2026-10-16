package dev.devaxes.composeatlas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.devaxes.composeatlas.ui.AtlasApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Automatic system-bar contrast follows the same OS light/dark choice as the app.
        enableEdgeToEdge()
        setContent { AtlasApp() }
    }
}
