package com.example.saferouter.presentation.reset

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saferouter.ui.theme.BackgroundWhite
import com.example.saferouter.ui.theme.PrimaryBlue
import com.example.saferouter.ui.theme.PrimaryBlueDark
import com.example.saferouter.ui.theme.TextPrimary
import com.example.saferouter.ui.theme.TextSecondary
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ResetPasswordScreen(auth: FirebaseAuth, navigateBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        Text(
            text = "Restablecer contraseña",
            color = PrimaryBlueDark,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Ingresa tu correo y te enviaremos un enlace para restablecer tu contraseña.",
            color = TextSecondary,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text("Correo electrónico", color = TextPrimary, fontWeight = FontWeight.Medium)
        TextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = BackgroundWhite,
                textColor = TextPrimary,
                cursorColor = PrimaryBlue,
                focusedIndicatorColor = PrimaryBlue,
                unfocusedIndicatorColor = TextSecondary,
                placeholderColor = TextSecondary
            ),
            placeholder = { Text("Ingresa tu correo") }
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = {
                if (email.isNotBlank()) {
                    isLoading = true
                    auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener { task ->
                            isLoading = false
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    context,
                                    "✅ Se envió un enlace a tu correo para restablecer tu contraseña.",
                                    Toast.LENGTH_LONG
                                ).show()
                                navigateBack()
                            } else {
                                Toast.makeText(
                                    context,
                                    "❌ Error: ${task.exception?.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                } else {
                    Toast.makeText(context, "⚠️ Ingresa tu correo", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = PrimaryBlue),
            elevation = ButtonDefaults.elevation(4.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
            } else {
                Text("Enviar enlace", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { navigateBack() }) {
            Text("Volver al login", color = PrimaryBlue)
        }
    }
}
