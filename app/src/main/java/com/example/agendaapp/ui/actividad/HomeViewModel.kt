package com.example.agendaapp.ui.actividad

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Agregar una Tarea"
    }
    val text: LiveData<String> = _text
}