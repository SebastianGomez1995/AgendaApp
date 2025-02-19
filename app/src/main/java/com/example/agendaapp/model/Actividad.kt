package com.example.agendaapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "actividades")
data class Actividad(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titulo: String,
    val descripcion: String,
    val fechaInicio: Long,
    val fechaFin: Long?,
    val tipo: TipoActividad,
    val frecuencia: Frecuencia,
    val estado: EstadoActividad,
    val notificacion: Boolean = false
)

enum class TipoActividad {
    TAREA, EVENTO, DIETA
}
enum class Frecuencia{
    DIARIA, SEMANAL, MENSUAL
}

enum class EstadoActividad {
    PENDIENTE, EN_PROCESO, COMPLETADA
}