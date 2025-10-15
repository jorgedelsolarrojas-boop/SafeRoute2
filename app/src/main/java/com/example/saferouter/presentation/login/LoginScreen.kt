package com.example.saferouter.presentation.login

// Importaciones principales para Jetpack Compose y Android
import androidx.compose.ui.platform.LocalContext
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saferouter.R
import com.example.saferouter.firebase.AuthManager
import com.example.saferouter.ui.theme.BackgroundWhite
import com.example.saferouter.ui.theme.PrimaryBlue
import com.example.saferouter.ui.theme.PrimaryBlueDark
import com.example.saferouter.ui.theme.TextPrimary
import com.example.saferouter.ui.theme.TextSecondary
import com.google.firebase.auth.FirebaseAuth

/**
 * Pantalla principal de inicio de sesión.
 * Permite al usuario autenticarse usando correo y contraseña mediante FirebaseAuth.
 *
 * Incluye validaciones básicas y manejo visual con Jetpack Compose.
 * @param auth instancia de FirebaseAuth utilizada para autenticar.
 * @param navigateToHome función callback que se ejecuta al iniciar sesión correctamente.
 * @param navigateToReset función opcional para ir a la pantalla de recuperación de contraseña.
 */
@Composable
fun LoginScreen(auth: FirebaseAuth, navigateToHome: () -> Unit, navigateToReset: () -> Unit = {}) {
    // Estados locales para el correo y la contraseña.
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Contexto actual de la aplicación (requerido para los Toast)
    val context = LocalContext.current

    // --- Layout principal ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Fila superior con ícono de retroceso (sin acción aún)
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back_24),
                contentDescription = "Back",
                tint = PrimaryBlueDark,
                modifier = Modifier
                    .padding(vertical = 24.dp)
                    .size(24.dp)
                    //.clickable { navigateBack() } // TODO: implementar navegación hacia atrás
            )
            Spacer(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(40.dp))

        // --- Título principal de la pantalla ---
        Text(
            "Login",
            color = PrimaryBlueDark,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Mensaje de bienvenida
        Text(
            "Welcome back! Please enter your details",
            color = TextSecondary,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        // --- Campo de texto: Email ---
        Text(
            "Email",
            color = TextPrimary,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.Start)
        )

        TextField(
            value = email,
            onValueChange = { email = it.trim() }, // Eliminamos espacios innecesarios
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = BackgroundWhite,
                textColor = TextPrimary,
                cursorColor = PrimaryBlue,
                focusedIndicatorColor = PrimaryBlue,
                unfocusedIndicatorColor = TextSecondary,
                placeholderColor = TextSecondary
            ),
            placeholder = { Text("Enter your email") }
        )

        Spacer(Modifier.height(32.dp))

        // --- Campo de texto: Contraseña ---
        Text(
            "Password",
            color = TextPrimary,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.Start)
        )

        TextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = BackgroundWhite,
                textColor = TextPrimary,
                cursorColor = PrimaryBlue,
                focusedIndicatorColor = PrimaryBlue,
                unfocusedIndicatorColor = TextSecondary,
                placeholderColor = TextSecondary
            ),
            placeholder = { Text("Enter your password") }
        )

        Spacer(Modifier.height(48.dp))

        // --- Botón de inicio de sesión ---
        Button(
            onClick = {
                // 🔹 Validación básica de entrada antes de intentar loguear
                when {
                    email.isBlank() -> {
                        Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                        Toast.makeText(context, "Invalid email format", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    password.isBlank() -> {
                        Toast.makeText(context, "Please enter your password", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    password.length < 6 -> {
                        Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                }

                // 🔹 Intento de login mediante AuthManager (Firebase)
                AuthManager.login(email, password) { success, message ->
                    if (success) {
                        Log.i("LoginScreen", "Login exitoso para $email")
                        navigateToHome()
                    } else {
                        Log.w("LoginScreen", "Error al iniciar sesión: $message")
                        Toast.makeText(context, message ?: "Error desconocido", Toast.LENGTH_LONG).show()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = PrimaryBlue)
        ) {
            Text(
                text = "Login",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Texto clicable: Recuperar contraseña ---
        Text(
            text = "Forgot password?",
            color = PrimaryBlue,
            modifier = Modifier.clickable { navigateToReset() },
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Comentario adicional para futuros desarrolladores ---
        // Aquí podría añadirse una opción para iniciar sesión con Google, Facebook, etc.
        // También podría incluirse un "Recordar sesión" o manejo de estado persistente.
    }
}

/**
 * Preview para visualizar el diseño de la pantalla de login sin ejecutar la app completa.
 * Esta vista previa es útil durante el desarrollo UI con Compose.
 */
@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen(auth = FirebaseAuth.getInstance(), navigateToHome = {})
}
