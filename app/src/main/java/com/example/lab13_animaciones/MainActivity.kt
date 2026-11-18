package com.example.lab13_animaciones

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lab13_animaciones.ui.theme.Lab13_AnimacionesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab13_AnimacionesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AnimatedContentScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

enum class UiState {
    Loading, Content, Error
}

@Composable
fun AnimatedContentScreen(modifier: Modifier = Modifier) {
    var currentState by remember { mutableStateOf(UiState.Loading) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            currentState = when (currentState) {
                UiState.Loading -> UiState.Content
                UiState.Content -> UiState.Error
                UiState.Error -> UiState.Loading
            }
        }) {
            Text(text = "Cambiar Estado")
        }

        Spacer(modifier = Modifier.height(24.dp))

        AnimatedContent(
            targetState = currentState,
            transitionSpec = {
                fadeIn(animationSpec = tween(500)) togetherWith fadeOut(animationSpec = tween(500))
            },
            label = "AnimatedContentTransition"
        ) { targetState ->
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .background(
                        when (targetState) {
                            UiState.Loading -> Color.Gray
                            UiState.Content -> Color.Green
                            UiState.Error -> Color.Red
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (targetState) {
                        UiState.Loading -> "Cargando..."
                        UiState.Content -> "¡Contenido Exitoso!"
                        UiState.Error -> "¡Error en la carga!"
                    },
                    color = Color.White
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnimatedContentScreenPreview() {
    Lab13_AnimacionesTheme {
        AnimatedContentScreen()
    }
}



