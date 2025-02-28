package com.example.agendaapp.ui.slideshow


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.agendaapp.databinding.ItemActividadHorarioBinding
import com.example.agendaapp.model.Horario
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ActividadHorarioAdapter(
    private var actividades: List<Horario>,
    private val onEditar: (Horario) -> Unit,
    private val onEliminar: (Horario) -> Unit
) : RecyclerView.Adapter<ActividadHorarioAdapter.ActividadHorarioViewHolder>() {

    inner class ActividadHorarioViewHolder(private val binding: ItemActividadHorarioBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(actividad: Horario) {
            binding.apply {
                tvMateria.text = actividad.materia
                tvSalon.text = "Salón: ${actividad.salon}"
                tvDiaSemana.text = "Día: ${actividad.dia}"

                // Formatear horas
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                tvHoraInicio.text = "Inicio: ${timeFormat.format(Date(actividad.horaInicio))}"
                tvHoraFin.text = "Fin: ${timeFormat.format(actividad.horaFin?.let { Date(it) })}"

                // Mostrar icono de notificación si está activada
                iconNotificacion.visibility = if (actividad.notificacion) View.VISIBLE else View.GONE

                // Configurar botones
                btnEditar.setOnClickListener { onEditar(actividad) }
                btnEliminar.setOnClickListener { onEliminar(actividad) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActividadHorarioViewHolder {
        val binding = ItemActividadHorarioBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ActividadHorarioViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ActividadHorarioViewHolder, position: Int) {
        holder.bind(actividades[position])
    }

    override fun getItemCount() = actividades.size

    fun actualizarLista(nuevasActividades: List<Horario>) {
        actividades = nuevasActividades
        notifyDataSetChanged()
    }
}
