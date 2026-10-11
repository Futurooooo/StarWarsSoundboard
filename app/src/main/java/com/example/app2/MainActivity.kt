package com.example.app2

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Un boton: su imagen y todos los sonidos de res/raw que tiene asignados. */
data class Personaje(val imagen: Int, val sonidos: List<Int>)

val PERSONAJES = listOf(
    Personaje(R.drawable.anakin, listOf(R.raw.anakin_1, R.raw.anakin_2, R.raw.anakin_3, R.raw.anakin_4, R.raw.anakin_5)),
    Personaje(R.drawable.yoda, listOf(R.raw.yoda)),
    Personaje(R.drawable.chewie, listOf(R.raw.chewie)),
    Personaje(R.drawable.han, listOf(R.raw.han)),
    Personaje(R.drawable.luke, listOf(R.raw.luke)),
    Personaje(R.drawable.lando, listOf(R.raw.lando)),
    Personaje(R.drawable.saber, listOf(R.raw.saber)),
    Personaje(R.drawable.vader, listOf(R.raw.vader))
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Interfaz()
        }
    }
}

@Composable
fun Interfaz() {
    val contexto = LocalContext.current
    var reproductor by remember { mutableStateOf<MediaPlayer?>(null) }
    var indice by remember { mutableStateOf(0) }
    var sonando by remember { mutableStateOf<Personaje?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            reproductor?.release()
            reproductor = null
        }
    }

    fun parar() {
        reproductor?.release()
        reproductor = null
        sonando = null
    }

    fun reproducir(personaje: Personaje) {
        parar()
        sonando = personaje
        indice = 0
        fun siguiente() {
            val lista = personaje.sonidos
            if (indice < lista.size) {
                val actual = lista[indice]
                indice = indice + 1
                reproductor = MediaPlayer.create(contexto, actual)?.apply {
                    setOnCompletionListener { mp ->
                        mp.release()
                        if (reproductor === mp) {
                            reproductor = null
                            siguiente()
                        }
                    }
                    start()
                }
            } else {
                parar()
            }
        }
        siguiente()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Junkbox Star Wars", fontSize = 24.sp)

        Text(
            text = sonando?.let { "sonando: ${indice}/${it.sonidos.size}" } ?: "elige un personaje",
            fontSize = 14.sp
        )

        PERSONAJES.chunked(3).forEach { fila ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
            ) {
                fila.forEach { personaje ->
                    BotonPersonaje(personaje = personaje, onPulsar = { reproducir(personaje) })
                }
            }
        }
    }
}

@Composable
fun BotonPersonaje(personaje: Personaje, onPulsar: () -> Unit) {
    Button(
        onClick = onPulsar,
        modifier = Modifier.size(96.dp),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(4.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF23233A))
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(personaje.imagen),
                contentDescription = null,
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = "${personaje.sonidos.size} sonido(s)",
                fontSize = 9.sp,
                color = Color.White
            )
        }
    }
}
