package com.example.saferouter.data

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.saferouter.R
import com.example.saferouter.presentation.model.Perfil
import com.example.saferouter.ui.theme.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.io.DataOutputStream

@Composable
fun PerfilScreen(
    db: FirebaseFirestore,
    navigateBack: () -> Unit,
    navigateToContacts: () -> Unit
) {
    val context = LocalContext.current
    val perfil = remember { mutableStateOf<Perfil?>(null) }
    val auth = FirebaseAuth.getInstance()
    val uid = auth.currentUser?.uid

    val cloudName = "djh5bpb3l"
    val uploadPreset = "android_unsigned"

    // 📸 Seleccionar imagen y subirla a Cloudinary
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            if (uid != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val imageUrl = uploadImageToCloudinary(selectedUri, cloudName, uploadPreset, context)
                        if (imageUrl != null) {
                            db.collection("users").document(uid)
                                .update("imageUrl", imageUrl)
                                .await()
                            withContext(Dispatchers.Main) {
                                perfil.value = perfil.value?.copy(imageUrl = imageUrl)
                                Toast.makeText(context, "✅ Imagen actualizada", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "❌ Error al subir la imagen", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "❌ ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    // 🔹 Cargar datos del perfil desde Firestore
    LaunchedEffect(uid) {
        if (uid != null) {
            try {
                val snapshot = db.collection("users").document(uid).get().await()
                if (snapshot.exists()) {
                    perfil.value = Perfil(
                        name = snapshot.getString("name") ?: "",
                        email = snapshot.getString("email") ?: "",
                        imageUrl = snapshot.getString("imageUrl") ?: "",
                        lastname = snapshot.getString("lastname") ?: "",
                        age = snapshot.getLong("age")?.toInt() ?: 0
                    )
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error cargando datos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    if (perfil.value == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryBlue)
        }
        return
    }

    // 🧠 UI del perfil
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
    ) {
        // 🔹 Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = navigateBack) {
                Icon(
                    painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_back_24),
                    contentDescription = "Back",
                    tint = PrimaryBlueDark
                )
            }
            Text(
                text = "Perfil de usuario",
                color = PrimaryBlueDark,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 📷 Imagen de perfil editable
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlueLight)
                    .clickable { imagePicker.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (!perfil.value?.imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = perfil.value!!.imageUrl,
                        contentDescription = "Profile photo",
                        modifier = Modifier.size(120.dp).clip(CircleShape)
                    )
                } else {
                    Text("Cambiar foto", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 🧾 Información del usuario
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                backgroundColor = BackgroundWhite
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    ProfileField("Nombre", perfil.value!!.name)
                    ProfileField("Apellido", perfil.value!!.lastname)
                    ProfileField("Edad", perfil.value!!.age.toString())
                    ProfileField("Correo", perfil.value!!.email)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = navigateToContacts,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = PrimaryBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Contactos", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = navigateBack,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = PrimaryBlueDark),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Aceptar", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// 🌥️ Subida de imagen a Cloudinary (sin backend)
suspend fun uploadImageToCloudinary(
    uri: Uri,
    cloudName: String,
    uploadPreset: String,
    context: android.content.Context
): String? = withContext(Dispatchers.IO) {
    try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes() ?: return@withContext null
        inputStream.close()

        val boundary = "Boundary-${System.currentTimeMillis()}"
        val lineEnd = "\r\n"
        val twoHyphens = "--"

        val url = URL("https://api.cloudinary.com/v1_1/$cloudName/image/upload")
        val connection = url.openConnection() as HttpURLConnection
        connection.doOutput = true
        connection.useCaches = false
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")

        val outputStream = DataOutputStream(connection.outputStream)
        outputStream.writeBytes(twoHyphens + boundary + lineEnd)
        outputStream.writeBytes("Content-Disposition: form-data; name=\"upload_preset\"$lineEnd$lineEnd")
        outputStream.writeBytes(uploadPreset + lineEnd)

        outputStream.writeBytes(twoHyphens + boundary + lineEnd)
        outputStream.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"image.jpg\"$lineEnd")
        outputStream.writeBytes("Content-Type: image/jpeg$lineEnd$lineEnd")
        outputStream.write(bytes)
        outputStream.writeBytes(lineEnd)
        outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd)
        outputStream.flush()
        outputStream.close()

        val responseCode = connection.responseCode
        if (responseCode == 200) {
            val response = connection.inputStream.bufferedReader().use { it.readText() }
            val urlStart = response.indexOf("\"secure_url\":\"") + 14
            val urlEnd = response.indexOf("\"", urlStart)
            return@withContext response.substring(urlStart, urlEnd).replace("\\/", "/")
        } else {
            Log.e("Cloudinary", "Error: ${connection.responseMessage}")
            null
        }
    } catch (e: Exception) {
        Log.e("Cloudinary", "Error subiendo imagen: ${e.message}")
        null
    }
}

@Composable
fun ProfileField(label: String, value: String) {
    Text(label, color = TextSecondary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    Text(
        text = value,
        color = TextPrimary,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 16.dp)
    )
}
