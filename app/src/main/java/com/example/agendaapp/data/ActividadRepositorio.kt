package com.example.agendaapp.data


import com.example.agendaapp.model.Actividad
import com.example.agendaapp.model.TipoActividad
import kotlinx.coroutines.flow.Flow

class ActividadRepositorio(private val actividadDao: ActividadDao) {
    val todasLasActividades: Flow<List<Actividad>> = actividadDao.obtenerTodas()

    fun obtenerPorRangoFecha(inicio: Long, fin: Long): Flow<List<Actividad>> =
        actividadDao.obtenerPorRangoFecha(inicio, fin)

    fun obtenerPorTipo(tipo: TipoActividad): Flow<List<Actividad>> =
        actividadDao.obtenerPorTipo(tipo)

    // Asegurarse de que el DAO devuelve Flow<Actividad?>
    fun obtenerPorId(id: Int): Flow<Actividad?> = actividadDao.obtenerPorId(id)

    suspend fun insertar(actividad: Actividad) {
        actividadDao.insertar(actividad)
    }

    suspend fun actualizar(actividad: Actividad) {
        actividadDao.actualizar(actividad)
    }

    suspend fun eliminar(actividad: Actividad) {
        actividadDao.eliminar(actividad)
    }
}
