package com.example.agendaapp.ui.home

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.agendaapp.AgendaApplication
import com.example.agendaapp.databinding.FragmentNuevaActividadBinding
import com.example.agendaapp.model.Actividad
import com.example.agendaapp.model.EstadoActividad
import com.example.agendaapp.model.Frecuencia
import com.example.agendaapp.model.TipoActividad
import kotlinx.coroutines.launch
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

    private var actividadId: Int? = null  // ID de la actividad en edición (nulo si es nueva)

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

        
        arguments?.let {
            val id = NuevaActividadFragmentArgs.fromBundle(it).actividadId
            actividadId = if (id == -1) null else id
        }

        configurarSpinners()
        configurarDateTimePickers()
        configurarBotones()

        // Si estamos editando, cargar los datos
        actividadId?.let { id ->
            cargarDatosActividad(id)
        }
    }

    private fun configurarSpinners() {
        val tiposActividad = TipoActividad.values().map { it.name }
        val frecuencia = Frecuencia.values().map { it.name }
        val estados = EstadoActividad.values().map { it.name }

        binding.spinnerTipoActividad.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, tiposActividad)
        )
        binding.spinnerFrecuencia.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, frecuencia)
        )
        binding.spinnerEstado.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, estados)
        )
    }

    private fun configurarDateTimePickers() {
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

    private fun cargarDatosActividad(id: Int) {
        lifecycleScope.launch {
            val actividad = viewModel.obtenerActividadPorId(id)
            actividad?.let {
                binding.etTitulo.setText(it.titulo)
                binding.etDescripcion.setText(it.descripcion)
                binding.spinnerTipoActividad.setText(it.tipo.name, false)
                binding.spinnerFrecuencia.setText(it.frecuencia.name, false)
                binding.spinnerEstado.setText(it.estado.name, false)

                val fecha = Date(it.fechaInicio)
                binding.etFechaInicio.setText(dateFormatter.format(fecha))
                binding.etHoraInicio.setText(timeFormatter.format(fecha))

                binding.switchNotificacion.isChecked = it.notificacion
            }
        }
    }

    private fun guardarActividad() {
        try {
            val nuevaActividad = Actividad(
                id = if (actividadId == null) 0 else actividadId!!,  // Solo usa el ID si estás editando
                titulo = binding.etTitulo.text.toString(),
                descripcion = binding.etDescripcion.text.toString(),
                fechaInicio = calendar.timeInMillis,
                fechaFin = calendar.timeInMillis + 3600000,
                tipo = TipoActividad.valueOf(binding.spinnerTipoActividad.text.toString()),
                frecuencia = Frecuencia.valueOf(binding.spinnerFrecuencia.text.toString()),
                estado = EstadoActividad.valueOf(binding.spinnerEstado.text.toString()),
                notificacion = binding.switchNotificacion.isChecked
            )
            Log.d("NuevaActividadFragment", "🟢 Valor inicial de actividadId: $actividadId")

            lifecycleScope.launch {
                if (actividadId == null) {
                    viewModel.agregarActividad(nuevaActividad)
                    Log.d("NuevaActividadFragment", "✅ Actividad agregada con ID: ${nuevaActividad.id}")
                    Toast.makeText(requireContext(), "Actividad creada", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.actualizarActividad(nuevaActividad)
                    Log.d("NuevaActividadFragment", "✏️ Actividad actualizada con ID: ${nuevaActividad.id}")
                    Toast.makeText(requireContext(), "Actividad actualizada", Toast.LENGTH_SHORT).show()
                }

                findNavController().navigateUp()
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            Log.e("NuevaActividadFragment", "❌ Error al guardar actividad", e)
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
