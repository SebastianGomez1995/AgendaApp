package com.example.agendaapp.util

import androidx.room.TypeConverter
import com.example.agendaapp.model.EstadoActividad
import com.example.agendaapp.model.TipoActividad

class Converters {
    @TypeConverter
    fun fromTipoActividad(tipo: TipoActividad): String {
        return tipo.name
    }

    @TypeConverter
    fun toTipoActividad(nombre: String): TipoActividad {
        return TipoActividad.valueOf(nombre)
    }

    @TypeConverter
    fun fromEstadoActividad(estado: EstadoActividad): String {
        return estado.name
    }

    @TypeConverter
    fun toEstadoActividad(nombre: String): EstadoActividad {
        return EstadoActividad.valueOf(nombre)
    }
}