package com.puc.pi3_es_2024_t24

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.TypeConverters

@Entity (tableName = "client")
data class Client (
    @PrimaryKey val cpf: String,
    @ColumnInfo (name= "cardName") val cardName: String,
    @ColumnInfo (name= "cardNumber") val cardNumber: String,
    @ColumnInfo (name= "cardCvv") val cardCvv: String,
    @ColumnInfo (name= "cardValidation") val cardValidation: String,
    @ColumnInfo (name= "name") val name: String,
    @ColumnInfo (name="phone") val phone: String,
    @ColumnInfo (name="email") val email: String,
    @ColumnInfo (name="birth") val birth: String
)