package com.example.agendaapp.ui.slideshow


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.agendaapp.data.HorarioRepositorio

class ActividadHorarioViewModelFactory(private val repositorio: HorarioRepositorio) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ActividadHorarioViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ActividadHorarioViewModel(repositorio) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
