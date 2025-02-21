package com.example.agendaapp.ui.home

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.agendaapp.AgendaApplication
import com.example.agendaapp.databinding.FragmentNuevaActividadBinding
import com.example.agendaapp.model.Actividad
import com.example.agendaapp.model.EstadoActividad
import com.example.agendaapp.model.Frecuencia
import com.example.agendaapp.model.TipoActividad
import java.text.SimpleDateFormat
import java.util.*

class NuevaActividadFragment : Fragment() {

    private var _binding: FragmentNuevaActividadBinding? = null
    private val binding get() = _binding!!
    
    private val calendar = Calendar.getInstance()
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    
    private val aplicacion: AgendaApplication by lazy {
        requireActivity().application as AgendaApplication
    }
    
    private val viewModel: ActividadViewModel by viewModels {
        ActividadViewModelFactory(aplicacion.repositorio)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNuevaActividadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        configurarSpinners()
        configurarDateTimePickers()
        configurarBotones()
    }

    private fun configurarSpinners() {
        // Configurar spinner de tipo de actividad
        val tiposActividad = TipoActividad.values().map { it.name }
        val tiposAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            tiposActividad
        )
        binding.spinnerTipoActividad.setAdapter(tiposAdapter)

        // Configurar spinner de frecuencia
        val frecuencia = Frecuencia.values().map { it.name }
        val frecuenciaAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            frecuencia
        )
        binding.spinnerFrecuencia.setAdapter(frecuenciaAdapter)

        // Configurar spinner de estado
        val estados = EstadoActividad.values().map { it.name }
        val estadosAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            estados
        )
        binding.spinnerEstado.setAdapter(estadosAdapter)
    }

    private fun configurarDateTimePickers() {
        // Configurar selector de fecha
        binding.etFechaInicio.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    binding.etFechaInicio.setText(dateFormatter.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Configurar selector de hora
        binding.etHoraInicio.setOnClickListener {
            TimePickerDialog(
                requireContext(),
                { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    binding.etHoraInicio.setText(timeFormatter.format(calendar.time))
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }
    }

    private fun configurarBotones() {
        binding.btnGuardar.setOnClickListener {
            if (validarCampos()) {
                guardarActividad()
            }
        }

        binding.btnCancelar.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun validarCampos(): Boolean {
        var esValido = true

        if (binding.etTitulo.text.isNullOrEmpty()) {
            binding.tilTitulo.error = "El título es requerido"
            esValido = false
        } else {
            binding.tilTitulo.error = null
        }

        if (binding.etFechaInicio.text.isNullOrEmpty()) {
            binding.tilFechaInicio.error = "La fecha es requerida"
            esValido = false
        } else {
            binding.tilFechaInicio.error = null
        }

        if (binding.etHoraInicio.text.isNullOrEmpty()) {
            binding.tilHoraInicio.error = "La hora es requerida"
            esValido = false
        } else {
            binding.tilHoraInicio.error = null
        }

        return esValido
    }

    private fun guardarActividad() {
        try {
            val actividad = Actividad(
                titulo = binding.etTitulo.text.toString(),
                descripcion = binding.etDescripcion.text.toString(),
                fechaInicio = calendar.timeInMillis,
                fechaFin = Date().time + 3600000,
                tipo = TipoActividad.valueOf(binding.spinnerTipoActividad.text.toString()),
                frecuencia = Frecuencia.valueOf(binding.spinnerFrecuencia.text.toString()),
                estado = EstadoActividad.valueOf(binding.spinnerEstado.text.toString()),
                notificacion = binding.switchNotificacion.isChecked
            )

            viewModel.agregarActividad(actividad)
            Toast.makeText(requireContext(), "Actividad guardada", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
            
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Error al guardar: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

