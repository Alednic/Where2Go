package org.umn.where2go

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import org.umn.where2go.navigation.AppNav
import org.umn.where2go.ui.theme.Where2GoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { Where2GoTheme { AppNav() } }
    }
}