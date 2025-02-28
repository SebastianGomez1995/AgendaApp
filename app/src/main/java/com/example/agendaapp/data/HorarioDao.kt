package com.example.agendaapp.data

import androidx.room.*
import com.example.agendaapp.model.Horario

import kotlinx.coroutines.flow.Flow

@Dao
interface HorarioDao {
    @Query("SELECT * FROM horario ORDER BY dia")
    fun obtenerHorario(): Flow<List<Horario>>


    @Query("SELECT * FROM horario WHERE id = :id LIMIT 1")
    fun obtenerPorId(id: Int): Flow<Horario?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(horario: Horario)

    @Update
    suspend fun actualizar(horario: Horario)

    @Delete
    suspend fun eliminar(horario: Horario)
}