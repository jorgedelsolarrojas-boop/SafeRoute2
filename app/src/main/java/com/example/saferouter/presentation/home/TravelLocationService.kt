package com.example.saferouter.presentation.home

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.saferouter.R
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class TravelLocationService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val channelId = "travel_tracking_channel"

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Crear canal de notificación para el servicio en primer plano
        createNotificationChannel()

        // Configurar callback de ubicación
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                for (location in result.locations) {
                    sendLocationToFirebase(location)
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("TravelLocationService", "Servicio iniciado")

        // Crear notificación persistente
        val notification = createForegroundNotification()
        startForeground(1, notification)

        // Iniciar actualizaciones de ubicación
        startLocationUpdates()

        return START_STICKY
    }

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 5000L // cada 5 segundos
        ).setMinUpdateIntervalMillis(3000L).build()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w("TravelLocationService", "Permisos de ubicación no concedidos")
            return
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, mainLooper)
    }

    private fun sendLocationToFirebase(location: Location) {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val dbRef = FirebaseDatabase.getInstance().reference
            .child("viajes")
            .child(user.uid)
            .child("ubicacion")

        val data = mapOf(
            "lat" to location.latitude,
            "lng" to location.longitude,
            "timestamp" to System.currentTimeMillis()
        )

        dbRef.push().setValue(data)
            .addOnSuccessListener {
                Log.d("TravelLocationService", "Ubicación enviada correctamente: $data")
            }
            .addOnFailureListener {
                Log.e("TravelLocationService", "Error al enviar ubicación: ${it.message}")
            }
    }

    private fun createForegroundNotification(): Notification {
        val notificationIntent = Intent(this, Class.forName("com.example.saferouter.MainActivity"))
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Seguimiento de viaje activo")
            .setContentText("Tu ubicación se está compartiendo en tiempo real.")
            .setSmallIcon(R.drawable.viaje) // asegúrate de tener este ícono en res/drawable
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                channelId,
                "Seguimiento de Viaje",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        Log.d("TravelLocationService", "Servicio detenido")
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
