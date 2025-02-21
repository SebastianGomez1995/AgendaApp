package com.example.agendaapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agendaapp.data.ActividadRepositorio
import com.example.agendaapp.model.Actividad
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ActividadViewModel(private val repositorio: ActividadRepositorio) : ViewModel() {

    val listaActividades: Flow<List<Actividad>> = repositorio.todasLasActividades

    fun agregarActividad(actividad: Actividad) {
        viewModelScope.launch {
            repositorio.insertar(actividad)
        }
    }

    fun eliminarActividad(actividad: Actividad) {
        viewModelScope.launch {
            repositorio.eliminar(actividad)
        }
    }
}
