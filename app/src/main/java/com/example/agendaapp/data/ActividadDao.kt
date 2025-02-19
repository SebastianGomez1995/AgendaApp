package com.example.agendaapp.data

import androidx.room.*
import com.example.agendaapp.model.Actividad
import com.example.agendaapp.model.TipoActividad
import kotlinx.coroutines.flow.Flow

@Dao
interface ActividadDao {
    @Query("SELECT * FROM actividades ORDER BY fechaInicio")
    fun obtenerTodas(): Flow<List<Actividad>>

    @Query("SELECT * FROM actividades WHERE fechaInicio >= :inicio AND fechaInicio <= :fin ORDER BY fechaInicio")
    fun obtenerPorRangoFecha(inicio: Long, fin: Long): Flow<List<Actividad>>

    @Query("SELECT * FROM actividades WHERE tipo = :tipo ORDER BY fechaInicio")
    fun obtenerPorTipo(tipo: TipoActividad): Flow<List<Actividad>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(actividad: Actividad)

    @Update
    suspend fun actualizar(actividad: Actividad)

    @Delete
    suspend fun eliminar(actividad: Actividad)
}