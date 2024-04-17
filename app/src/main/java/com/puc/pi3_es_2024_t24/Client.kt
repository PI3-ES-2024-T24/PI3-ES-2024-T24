package com.puc.pi3_es_2024_t24

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

data class Card(
    val name: String,
    val number: String,
    val cvv: String,
    val validation: String
)

@Entity
data class Client (
    @PrimaryKey val cpf: String,
    @ColumnInfo (name= "card") val card: Card,
    @ColumnInfo (name= "name") val name: String,
    @ColumnInfo (name="phone") val phone: String,
    @ColumnInfo (name="email") val email: String
)