package com.example.agendaapp.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.agendaapp.databinding.FragmentHomeBinding
import com.example.agendaapp.model.Actividad
import com.example.agendaapp.model.TipoActividad
import com.example.agendaapp.AgendaApplication
import com.example.agendaapp.model.EstadoActividad
import androidx.lifecycle.lifecycleScope
import com.example.agendaapp.model.Frecuencia
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Date

class HomeFragment : Fragment() {

private var _binding: FragmentHomeBinding? = null
  private val binding get() = _binding!!
  private lateinit var aplicacion: AgendaApplication


  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

    _binding = FragmentHomeBinding.inflate(inflater, container, false)
    val root: View = binding.root

    val textView: TextView = binding.textHome
    homeViewModel.text.observe(viewLifecycleOwner) {
      textView.text = it
    }
    aplicacion = requireActivity().application as AgendaApplication

    binding.buttonAdd.setOnClickListener {
      probarBaseDeDatos()
     }
    return root

  }

override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

  private fun probarBaseDeDatos() {


    lifecycleScope.launch {
      // Crear una actividad de prueba
      val actividadPrueba = Actividad(
        titulo = "Actividad de prueba",
        descripcion = "test",
        fechaInicio = Date().time,
        fechaFin = Date().time + 3600000, // 1 hora después
        tipo = TipoActividad.TAREA,
        frecuencia = Frecuencia.DIARIA,
        estado = EstadoActividad.PENDIENTE
      )

      try {
        // Insertar en la base de datos
        aplicacion.repositorio.insertar(actividadPrueba)

        // Recuperar todas las actividades
        val actividades = aplicacion.repositorio.todasLasActividades.first()

        // Mostrar resultado
        val mensaje = if (actividades.isNotEmpty()) {
          "¡Prueba exitosa! Actividades en BD: ${actividades.size}"
        } else {
          "Error: No se encontraron actividades"
        }

        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_LONG).show()
        Log.d("Agenda", "Iniciando prueba...")
        Log.d("Agenda", "Actividad insertada con éxito")
        Log.d("Agenda", "Recuperadas ${actividades.size} actividades")



      } catch (e: Exception) {
        Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()

      }
    }
  }
}
