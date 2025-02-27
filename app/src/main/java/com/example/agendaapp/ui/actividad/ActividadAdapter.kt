package com.example.agendaapp.ui.actividad

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.agendaapp.databinding.ItemActividadBinding
import com.example.agendaapp.model.Actividad
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ActividadAdapter(
    private var actividades: List<Actividad>,
    private val onEditar: (Actividad) -> Unit,
    private val onEliminar: (Actividad) -> Unit
) : RecyclerView.Adapter<ActividadAdapter.ActividadViewHolder>() {

    inner class ActividadViewHolder(private val binding: ItemActividadBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(actividad: Actividad) {
            binding.apply {
                tvTitulo.text = actividad.titulo
                tvDescripcion.text = actividad.descripcion
                tvTipo.text = "Tipo: ${actividad.tipo.name}"
                tvEstado.text = "Estado: ${actividad.estado.name}"
                tvFrecuencia.text = "Frecuencia: ${actividad.frecuencia.name}"

                // Formatear fechas
                val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                tvFechaInicio.text = "Inicio: ${dateFormat.format(Date(actividad.fechaInicio))}"
                actividad.fechaFin?.let {
                    tvFechaFin.text = "Fin: ${dateFormat.format(Date(it))}"
                }

                // Configurar botones
                btnEditar.setOnClickListener { onEditar(actividad) }
                btnEliminar.setOnClickListener { onEliminar(actividad) }

                // Mostrar indicador de notificación si está activada
                iconNotificacion.visibility = if (actividad.notificacion) View.VISIBLE else View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActividadViewHolder {
        val binding = ItemActividadBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ActividadViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ActividadViewHolder, position: Int) {
        holder.bind(actividades[position])
    }

    override fun getItemCount() = actividades.size

    fun actualizarLista(nuevasActividades: List<Actividad>) {
        actividades = nuevasActividades
        notifyDataSetChanged()
    }
}