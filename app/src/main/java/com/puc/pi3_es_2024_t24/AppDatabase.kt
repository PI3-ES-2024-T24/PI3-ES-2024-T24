package com.puc.pi3_es_2024_t24

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Client::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clientDao(): ClientDao
}