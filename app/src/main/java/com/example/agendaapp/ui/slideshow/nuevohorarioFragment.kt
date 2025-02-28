package com.example.agendaapp.ui.slideshow

import android.R
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
import com.example.agendaapp.databinding.FragmentNuevoHorarioBinding
import com.example.agendaapp.model.Horario
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class NuevoHorarioFragment : Fragment() {

    private var _binding: FragmentNuevoHorarioBinding? = null
    private val binding get() = _binding!!

    private val calendar = Calendar.getInstance()
    private val calendar2 = Calendar.getInstance()
    private val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

    private val aplicacion: AgendaApplication by lazy {
        requireActivity().application as AgendaApplication
    }

    private val viewModel: ActividadHorarioViewModel by viewModels {
        ActividadHorarioViewModelFactory(aplicacion.horarioRepo)
    }

    private var horarioId: Int? = null  // ID del horario en edición (nulo si es nuevo)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNuevoHorarioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            val id = NuevoHorarioFragmentArgs.fromBundle(it).horarioId
            horarioId = if (id == -1) null else id
        }

        configurarSpinners()
        configurarTimePickers()
        configurarBotones()

        horarioId?.let { id ->
            cargarDatosHorario(id)
        }
    }

    private fun configurarSpinners() {
        val diasSemana = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
        binding.spinnerDia.setAdapter(
            ArrayAdapter(requireContext(), R.layout.simple_dropdown_item_1line, diasSemana)
        )
    }

    private fun configurarTimePickers() {
        binding.btnHoraInicio.setOnClickListener {
            TimePickerDialog(
                requireContext(),
                { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    binding.btnHoraInicio.text = timeFormatter.format(calendar.time)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }
        Log.d("NuevoHorarioFragment", "Antes de guardar - ⏰ Hora Inicio: ${calendar.timeInMillis}")
        Log.d("NuevoHorarioFragment", "Antes de guardar - ⏰ Hora Fin: ${calendar.timeInMillis + 3600000}")


        binding.btnHoraFin.setOnClickListener {
            TimePickerDialog(
                requireContext(),
                { _, hourOfDay, minute ->
                    calendar2.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar2.set(Calendar.MINUTE, minute)
                    binding.btnHoraFin.text = timeFormatter.format(calendar2.time)
                },
                calendar2.get(Calendar.HOUR_OF_DAY),
                calendar2.get(Calendar.MINUTE),
                true
            ).show()
        }
    }

    private fun configurarBotones() {
        binding.btnGuardar.setOnClickListener {
            if (validarCampos()) {
                guardarHorario()
            }
        }

        binding.btnCancelar.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun validarCampos(): Boolean {
        var esValido = true

        if (binding.editTextMateria.text.isNullOrEmpty()) {
            binding.editTextMateria.error = "La materia es requerida"
            esValido = false
        } else {
            binding.editTextMateria.error = null
        }

        if (binding.editTextSalon.text.isNullOrEmpty()) {
            binding.editTextSalon.error = "El salón es requerido"
            esValido = false
        } else {
            binding.editTextSalon.error = null
        }

        if (binding.btnHoraInicio.text == "Seleccionar Hora Inicio") {
            Toast.makeText(requireContext(), "Debe seleccionar una hora de inicio", Toast.LENGTH_SHORT).show()
            esValido = false
        }

        if (binding.btnHoraFin.text == "Seleccionar Hora Fin") {
            Toast.makeText(requireContext(), "Debe seleccionar una hora de fin", Toast.LENGTH_SHORT).show()
            esValido = false
        }

        return esValido
    }

    private fun cargarDatosHorario(id: Int) {
        lifecycleScope.launch {
            val horario = viewModel.obtenerHorarioPorId(id)
            horario?.let {
                binding.editTextMateria.setText(it.materia)
                binding.editTextSalon.setText(it.salon)

                // Corregimos para AutoCompleteTextView
                val position = (binding.spinnerDia.adapter as ArrayAdapter<String>).getPosition(it.dia)
                binding.spinnerDia.setSelection(position)

                binding.btnHoraInicio.text = timeFormatter.format(Date(it.horaInicio))
                binding.btnHoraFin.text = it.horaFin?.let { fecha -> timeFormatter.format(Date(fecha)) }
                binding.switchNotificacion.isChecked = it.notificacion
            }
        }
    }

    private fun guardarHorario() {
        try {
            val nuevoHorario = Horario(
                id = horarioId ?: 0,  // Si es nuevo, se asignará un ID automáticamente en la BD
                materia = binding.editTextMateria.text.toString(),
                salon = binding.editTextSalon.text.toString(),
                dia = binding.spinnerDia.selectedItem.toString(),
                horaInicio = calendar.timeInMillis,
                horaFin = calendar2.timeInMillis, // Asumimos una duración de 1 hora por defecto
                notificacion = binding.switchNotificacion.isChecked
            )

            lifecycleScope.launch {
                if (horarioId == null) {
                    viewModel.agregarHorario(nuevoHorario)
                    Toast.makeText(requireContext(), "Horario agregado", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.actualizarHorario(nuevoHorario)
                    Toast.makeText(requireContext(), "Horario actualizado", Toast.LENGTH_SHORT).show()
                }

                findNavController().navigateUp()
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            Log.e("NuevoHorarioFragment", "❌ Error al guardar horario", e)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
