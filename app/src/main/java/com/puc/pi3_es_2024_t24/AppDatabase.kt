package com.puc.pi3_es_2024_t24

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Client::class], version = 1, exportSchema = false)

abstract class AppDatabase : RoomDatabase() {
    abstract fun clientDao(): ClientDao

    companion object
    {
        @Volatile
        private var INSTANCE : AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase
        {
            return INSTANCE ?: synchronized(this)
            {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "client"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}