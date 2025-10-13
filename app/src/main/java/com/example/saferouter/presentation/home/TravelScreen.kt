package com.example.saferouter.presentation.home

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startForegroundService
import android.content.Intent

@Composable
fun TravelScreen(
    context: Context
) {
    var isTraveling by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F2F2)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = if (isTraveling) "🚗 Viaje en curso" else "🛑 No hay viaje activo",
            fontSize = 24.sp,
            color = if (isTraveling) Color.Green else Color.Red
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                isTraveling = !isTraveling
                if (isTraveling) {
                    val intent = Intent(context, TravelLocationService::class.java)
                    startForegroundService(context, intent)
                } else {
                    val intent = Intent(context, TravelLocationService::class.java)
                    context.stopService(intent)
                }
            },
            modifier = Modifier
                .height(56.dp)
                .width(220.dp)
        ) {
            Text(text = if (isTraveling) "Finalizar viaje" else "Iniciar viaje")
        }
    }
}
