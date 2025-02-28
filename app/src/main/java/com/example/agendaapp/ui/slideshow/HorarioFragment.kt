package com.example.agendaapp.ui.slideshow;

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.agendaapp.AgendaApplication
import com.example.agendaapp.R
import com.example.agendaapp.databinding.FragmentHorarioBinding
import com.example.agendaapp.model.Horario
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class HorarioFragment : Fragment() {

    private var _binding: FragmentHorarioBinding? = null
    private val binding get() = _binding!!
    private lateinit var actividadViewModel: ActividadHorarioViewModel
    private lateinit var actividadAdapter: ActividadHorarioAdapter
    private lateinit var aplicacion: AgendaApplication

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
  ): View {
        _binding = FragmentHorarioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupInitialConfig()
        setupRecyclerView()
        setupObservers()
        setupListeners()
    }

    private fun setupInitialConfig() {
        aplicacion = requireActivity().application as AgendaApplication
        val factory = ActividadHorarioViewModelFactory((requireActivity().application as AgendaApplication).horarioRepo)
        actividadViewModel = ViewModelProvider(this, factory)[ActividadHorarioViewModel::class.java]
    }

    private fun setupRecyclerView() {
        actividadAdapter = ActividadHorarioAdapter(
                actividades = emptyList(),
                onEditar = { actividad -> editarActividad(actividad) },
                onEliminar = { actividad -> mostrarDialogoConfirmacion(actividad) }
        )

        binding.recyclerHorario.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = actividadAdapter
            setHasFixedSize(true)
            itemAnimator = null
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            actividadViewModel.listaActividades.collect { actividades ->
                if (isAdded) {  // Verifica que el Fragmento sigue en la pantalla
                    actividadAdapter.actualizarLista(actividades)
                    actualizarVistaVacia(actividades.isEmpty())
                }
            }
        }
    }

    private fun setupListeners() {
        binding.buttonAdd.setOnClickListener {
            findNavController().navigate(R.id.nav_nuevo_horario)
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            lifecycleScope.launch {
                actividadViewModel.listaActividades
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun actualizarVistaVacia(isEmpty: Boolean) {
        if (isEmpty) {
            binding.emptyStateLayout.visibility = View.VISIBLE
            binding.recyclerHorario.visibility = View.GONE
        } else {
            binding.emptyStateLayout.visibility = View.GONE
            binding.recyclerHorario.visibility = View.VISIBLE
        }
    }

    private fun editarActividad(actividad: Horario) {
        val action = HorarioFragmentDirections.actionHorarioFragmentToNuevoHorario(actividad.id)
        findNavController().navigate(action)
    }

    private fun mostrarDialogoConfirmacion(actividad: Horario) {
        MaterialAlertDialogBuilder(requireContext())
                .setTitle("Eliminar Materia")
                .setMessage("¿Estás seguro que deseas eliminar '${actividad.materia}'?")
                .setPositiveButton("Eliminar") { _, _ ->
                eliminarActividad(actividad)
        }
      .setNegativeButton("Cancelar", null)
                .show()
    }

    private fun eliminarActividad(actividad: Horario) {
        lifecycleScope.launch {
            actividadViewModel.eliminarHorario(actividad)
            Toast.makeText(
                    requireContext(),
                    "Actividad eliminada: ${actividad.materia}",
                    Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onResume() {
        super.onResume()
        actividadViewModel.listaActividades
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
