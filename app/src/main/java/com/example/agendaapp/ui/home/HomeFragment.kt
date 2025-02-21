package com.example.agendaapp.ui.home

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
import com.example.agendaapp.databinding.FragmentHomeBinding
import com.example.agendaapp.model.Actividad
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

  private var _binding: FragmentHomeBinding? = null
  private val binding get() = _binding!!
  private lateinit var actividadViewModel: ActividadViewModel
  private lateinit var actividadAdapter: ActividadAdapter
  private lateinit var aplicacion: AgendaApplication

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentHomeBinding.inflate(inflater, container, false)
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
    val factory = ActividadViewModelFactory((requireActivity().application as AgendaApplication).repositorio)
    actividadViewModel = ViewModelProvider(this, factory)[ActividadViewModel::class.java]

  }

  private fun setupRecyclerView() {
    actividadAdapter = ActividadAdapter(
      actividades = emptyList(),
      onEditar = { actividad -> editarActividad(actividad) },
      onEliminar = { actividad -> mostrarDialogoConfirmacion(actividad) }
    )

    binding.recyclerActividades.apply {
      layoutManager = LinearLayoutManager(requireContext())
      adapter = actividadAdapter
      setHasFixedSize(true)
      itemAnimator = null
    }
  }

  private fun setupObservers() {
    lifecycleScope.launch {
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
      findNavController().navigate(R.id.nav_nueva_actividad)
    }

    binding.swipeRefreshLayout.setOnRefreshListener {
      lifecycleScope.launch {

        binding.swipeRefreshLayout.isRefreshing = false
      }
    }
  }

  private fun actualizarVistaVacia(isEmpty: Boolean) {
    if (isEmpty) {
      binding.emptyStateLayout.visibility = View.VISIBLE
      binding.recyclerActividades.visibility = View.GONE
    } else {
      binding.emptyStateLayout.visibility = View.GONE
      binding.recyclerActividades.visibility = View.VISIBLE
    }
  }

 private fun editarActividad(actividad: Actividad) {
   val action = HomeFragmentDirections.actionNavHomeToNuevaActividad(actividad.id)
   findNavController().navigate(action)

  }

  private fun mostrarDialogoConfirmacion(actividad: Actividad) {
    MaterialAlertDialogBuilder(requireContext())
      .setTitle("Eliminar actividad")
      .setMessage("¿Estás seguro que deseas eliminar '${actividad.titulo}'?")
      .setPositiveButton("Eliminar") { _, _ ->
        eliminarActividad(actividad)
      }
      .setNegativeButton("Cancelar", null)
      .show()
  }

  private fun eliminarActividad(actividad: Actividad) {
    lifecycleScope.launch {
      actividadViewModel.eliminarActividad(actividad)
      Toast.makeText(
        requireContext(),
        "Actividad eliminada: ${actividad.titulo}",
        Toast.LENGTH_SHORT
      ).show()
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}