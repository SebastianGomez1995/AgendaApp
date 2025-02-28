package com.example.agendaapp.data


import com.example.agendaapp.model.Horario

import kotlinx.coroutines.flow.Flow

class HorarioRepositorio(private val horarioDao: HorarioDao) {
    val todoElHorario: Flow<List<Horario>> = horarioDao.obtenerHorario()

    fun obtenerPorId(id: Int): Flow<Horario?> = horarioDao.obtenerPorId(id)

    suspend fun insertar(horario: Horario) {
        horarioDao.insertar(horario)
    }

    suspend fun actualizar(horario: Horario) {
        horarioDao.actualizar(horario)
    }

    suspend fun eliminar(horario: Horario) {
        horarioDao.eliminar(horario)
    }
}
