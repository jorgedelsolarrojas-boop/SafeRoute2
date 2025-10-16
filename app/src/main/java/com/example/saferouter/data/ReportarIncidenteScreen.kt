package com.example.saferouter.data
import com.example.saferouter.presentation.home.HomeScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saferouter.R
import com.example.saferouter.ui.theme.*
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ReportarIncidenteScreen(
    navigateBack: () -> Unit
) {
    val incidentType = remember { mutableStateOf("Robo") }
    val description = remember { mutableStateOf("") }
    val location = remember { mutableStateOf("Current location detected") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
    ) {
        // Header con botón de retroceso
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = navigateBack,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back_24),
                    contentDescription = "Back",
                    tint = PrimaryBlueDark
                )
            }
            Text(
                text = "Reportar Incidente",
                color = PrimaryBlueDark,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Tipo de incidente
            Text(
                text = "Tipo de incidente",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                elevation = 2.dp,
                shape = RoundedCornerShape(12.dp),
                backgroundColor = PrimaryBlueLight.copy(alpha = 0.1f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = incidentType.value,
                        color = PrimaryBlueDark,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back_24),
                        contentDescription = "Dropdown",
                        tint = PrimaryBlueDark,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Descripción
            Text(
                text = "Descripcion",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            TextField(
                value = description.value,
                onValueChange = { description.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(bottom = 20.dp),
                placeholder = { Text("Describe que pasó...") },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = BackgroundWhite,
                    textColor = TextPrimary,
                    cursorColor = PrimaryBlue,
                    focusedIndicatorColor = PrimaryBlue,
                    unfocusedIndicatorColor = TextSecondary
                )
            )

            // Ubicación
            Text(
                text = "Ubicación",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                elevation = 2.dp,
                shape = RoundedCornerShape(12.dp),
                backgroundColor = BackgroundWhite
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "📍 $location.value",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Cambiar ubicación",
                            color = PrimaryBlue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // Evidencia
            Text(
                text = "Evidencia (opcional)",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Button(
                onClick = { /* Lógica para tomar foto */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(bottom = 32.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = PrimaryBlueLight),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.elevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 4.dp
                )
            ) {
                Text(
                    text = "Tomar foto",
                    color = PrimaryBlueDark,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botón Enviar reporte
            Button(
                onClick = { /* Lógica para enviar reporte */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = SuccessGreen),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.elevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 8.dp
                )
            ) {
                Text(
                    text = "✓ Enviar Reporte",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Preview
@Composable
fun ReportarIncidenteScreenPreview() {
    ReportarIncidenteScreen(
        navigateBack = {}
    )
}

