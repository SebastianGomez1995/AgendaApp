package com.example.agendaapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "horario")
data class Horario(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val materia: String,
    val salon: String,
    val fechaInicio: Long,
    val fechaFin: Long?,
    val dia: diaSemana,
    val estado: Boolean = true,
    val notificacion: Boolean = false
)

enum class diaSemana {
    LUNES, MARTES, MIERCOLES, JUEVES, VIERNES, SABADO
}
