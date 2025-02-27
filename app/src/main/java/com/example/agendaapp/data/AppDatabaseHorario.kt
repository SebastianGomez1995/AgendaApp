package com.example.agendaapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.agendaapp.model.Horario
import com.example.agendaapp.util.Converters

@Database(entities = [Horario::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class HorarioDatabase : RoomDatabase() {
    abstract fun horarioDao(): HorarioDao

    companion object {
        @Volatile
        private var INSTANCE: HorarioDatabase? = null

        fun getDatabase(context: Context): HorarioDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HorarioDatabase::class.java,
                    "horario_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
