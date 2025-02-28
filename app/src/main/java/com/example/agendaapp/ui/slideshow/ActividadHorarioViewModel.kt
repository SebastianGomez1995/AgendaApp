package com.example.agendaapp.ui.slideshow



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agendaapp.data.HorarioRepositorio
import com.example.agendaapp.model.Horario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class ActividadHorarioViewModel(private val repositorio: HorarioRepositorio) : ViewModel() {

    val listaActividades: Flow<List<Horario>> = repositorio.todoElHorario

    fun agregarHorario(actividad: Horario) {
        viewModelScope.launch {
            repositorio.insertar(actividad)
        }
    }

    fun eliminarHorario(actividad: Horario) {
        viewModelScope.launch {
            repositorio.eliminar(actividad)
        }
    }

    fun actualizarHorario(actividad: Horario) {
        viewModelScope.launch {
            repositorio.actualizar(actividad)
        }
    }

    suspend fun obtenerHorarioPorId(id: Int): Horario? {
        return repositorio.obtenerPorId(id).firstOrNull()
    }


}
