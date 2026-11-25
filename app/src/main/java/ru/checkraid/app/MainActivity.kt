package ru.checkraid.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import ru.checkraid.app.ui.navigation.RaidControlApp
import ru.checkraid.app.ui.theme.RaidControlTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RaidControlTheme {
                RaidControlApp()
            }
        }
    }
}