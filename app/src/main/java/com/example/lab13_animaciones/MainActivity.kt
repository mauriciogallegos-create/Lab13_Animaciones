package com.example.lab13_animaciones

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lab13_animaciones.ui.theme.Lab13_AnimacionesTheme
import kotlinx.coroutines.delay
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab13_AnimacionesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ArcadePlaneGameScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

enum class GameState {
    Menu, Playing, GameOver
}

@Composable
fun ArcadePlaneGameScreen(modifier: Modifier = Modifier) {
    var gameState by remember { mutableStateOf(GameState.Menu) }
    var score by remember { mutableIntStateOf(0) }
    var playerLives by remember { mutableIntStateOf(3) }
    var bossHp by remember { mutableIntStateOf(3) }
    var planePosition by remember { mutableIntStateOf(0) } // -1: Left, 0: Center, 1: Right
    var bossPosition by remember { mutableIntStateOf(0) } // -1: Left, 0: Center, 1: Right
    var isShieldActive by remember { mutableStateOf(false) }
    var feedbackMessage by remember { mutableStateOf("") }
    var bossAttacking by remember { mutableStateOf(false) }

    // Automatic Boss Attack Loop using LaunchedEffect
    if (gameState == GameState.Playing) {
        LaunchedEffect(Unit) {
            while (true) {
                delay(3500L)
                if (bossHp > 0 && gameState == GameState.Playing) {
                    bossAttacking = true
                    feedbackMessage = "⚠️ ¡El jefe disparó un misil!"
                    if (!isShieldActive) {
                        playerLives--
                        if (playerLives <= 0) {
                            gameState = GameState.GameOver
                        }
                    } else {
                        feedbackMessage = "🛡️ ¡Escudo bloqueó el ataque del jefe!"
                    }
                    delay(1500L)
                    bossAttacking = false
                }
            }
        }
    }

    val defaultColor = Color(0xFF00E5FF)
    val shieldColor = Color(0xFFFFEA00)
    val planeAuraColor by animateColorAsState(
        targetValue = if (isShieldActive) shieldColor else defaultColor,
        animationSpec = tween(400),
        label = "PlaneAura"
    )

    val planeOffsetX by animateDpAsState(
        targetValue = (planePosition * 90).dp,
        animationSpec = tween(300),
        label = "PlaneOffset"
    )

    val bossOffsetX by animateDpAsState(
        targetValue = (bossPosition * 90).dp,
        animationSpec = tween(300),
        label = "BossOffset"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0A0F1D), Color(0xFF1B2A4A), Color(0xFF03050B))
                )
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedContent(
            targetState = gameState,
            transitionSpec = {
                fadeIn(animationSpec = tween(400)) togetherWith fadeOut(animationSpec = tween(400))
            },
            label = "GameStates"
        ) { state ->
            when (state) {
                GameState.Menu -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "SKY STRIKE ARCADE",
                            color = Color(0xFF00E5FF),
                            style = MaterialTheme.typography.headlineLarge,
                            fontSize = 26.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Combate aéreo con precisión",
                            color = Color.LightGray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(40.dp))
                        Button(
                            onClick = {
                                score = 0
                                playerLives = 3
                                bossHp = 3
                                planePosition = 0
                                bossPosition = 0
                                isShieldActive = false
                                feedbackMessage = "¡Inicia el combate!"
                                gameState = GameState.Playing
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00ACC1))
                        ) {
                            Text(text = "INICIAR MISIÓN", fontSize = 16.sp)
                        }
                    }
                }
                GameState.Playing -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(0.9f),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(width = 130.dp, height = 38.dp)
                                    .background(planeAuraColor.copy(alpha = 0.2f), RoundedCornerShape(19.dp))
                                    .border(1.5.dp, planeAuraColor, RoundedCornerShape(19.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "PTS: $score", color = Color.White, fontSize = 13.sp)
                            }

                            Text(
                                text = "VIDAS: " + "❤️".repeat(playerLives.coerceAtLeast(0)),
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Box(
                            modifier = Modifier
                                .size(width = 300.dp, height = 340.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF050B14))
                                .border(2.dp, Color(0xFF00ACC1), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .offset(x = bossOffsetX)
                                    .padding(top = 16.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    AnimatedVisibility(
                                        visible = bossHp > 0,
                                        enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
                                        exit = fadeOut() + slideOutVertically(targetOffsetY = { -it })
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Box(
                                                modifier = Modifier
                                                    .size(60.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(Color(0xFFD32F2F)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(text = "👾 BOSS", color = Color.White, fontSize = 11.sp)
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "HP: " + "█".repeat(bossHp),
                                                color = Color.Red,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(bottom = 60.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    AnimatedVisibility(
                                        visible = bossAttacking,
                                        enter = fadeIn(),
                                        exit = fadeOut()
                                    ) {
                                        Text(
                                            text = feedbackMessage,
                                            color = Color.Yellow,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(bottom = 10.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    AnimatedVisibility(
                                        visible = !bossAttacking && feedbackMessage.isNotEmpty(),
                                        enter = fadeIn(),
                                        exit = fadeOut()
                                    ) {
                                        Text(
                                            text = feedbackMessage,
                                            color = if (feedbackMessage.contains("¡IMPACTO")) Color.Green else Color.White,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .offset(x = planeOffsetX, y = (-30).dp)
                                    .size(65.dp)
                                    .clip(CircleShape)
                                    .background(planeAuraColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF1565C0)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "✈️", fontSize = 22.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth(0.9f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { if (planePosition > -1) planePosition-- },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF37474F))
                            ) {
                                Text(text = "◄ IZQ")
                            }
                            Button(
                                onClick = {
                                    if (planePosition == bossPosition) {
                                        bossHp--
                                        score += 100
                                        feedbackMessage = "💥 ¡IMPACTO DIRECTO!"
                                        if (bossHp == 0) {
                                            bossHp = 3
                                            score += 500
                                            bossPosition = Random.nextInt(-1, 2)
                                            feedbackMessage = "🏆 ¡BOSS DERROTADO!"
                                        }
                                    } else {
                                        feedbackMessage = "❌ ¡DISPARO AL AIRE!"
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63))
                            ) {
                                Text(text = "🔥 FUEGO")
                            }
                            Button(
                                onClick = { if (planePosition < 1) planePosition++ },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF37474F))
                            ) {
                                Text(text = "DER ►")
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            Button(
                                onClick = { isShieldActive = !isShieldActive },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF57F17))
                            ) {
                                Text(text = if (isShieldActive) "Escudo ON" else "Escudo OFF")
                            }
                            Button(
                                onClick = { gameState = GameState.GameOver },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C))
                            ) {
                                Text(text = "Salir")
                            }
                        }
                    }
                }
                GameState.GameOver -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "MISIÓN TERMINADA",
                            color = Color(0xFFFF5252),
                            style = MaterialTheme.typography.headlineLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Puntaje Final: $score",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = { gameState = GameState.Menu },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00ACC1))
                        ) {
                            Text(text = "Menú Principal")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ArcadePlaneGameScreenPreview() {
    Lab13_AnimacionesTheme {
        ArcadePlaneGameScreen()
    }
}
