package com.example.pokeapiMejorado

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pokeapiMejorado.ui.navigation.PokeApiApp
import com.example.pokeapiMejorado.ui.state.PokeViewModel
import com.example.pokeapiMejorado.ui.theme.PokeApiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokeApiTheme {
                val viewModel: PokeViewModel = viewModel()
                PokeApiApp(viewModel)

                }
            }
        }
    }


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PokeApiTheme {
        PokeApiApp()
    }
}