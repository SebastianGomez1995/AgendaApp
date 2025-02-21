package com.example.agendaapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.agendaapp.data.AppDatabase
import com.example.agendaapp.data.ActividadRepositorio
class AgendaApplication : Application() {
    // Instancia de la base de datos usando inicialización lazy
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }

    // Repositorio, también con inicialización lazy
    val repositorio: ActividadRepositorio by lazy { ActividadRepositorio(database.actividadDao()) }

    companion object {
        const val CHANNEL_ID = "actividades_channel"
    }

    override fun onCreate() {
        super.onCreate()
        crearCanalNotificaciones()
    }

    /**
     * Crea el canal de notificaciones para Android 8.0+
     */
    private fun crearCanalNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nombre = getString(R.string.channel_name)
            val descripcion = getString(R.string.channel_description)
            val importancia = NotificationManager.IMPORTANCE_DEFAULT
            val canal = NotificationChannel(CHANNEL_ID, nombre, importancia).apply {
                description = descripcion
            }

            // Registrar el canal con el sistema
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(canal)
        }
    }
}