package com.puc.pi3_es_2024_t24

data class Client(
    val cpf: String,
    val email: String,
    val nome: String,
    val dataNascimento: String,
    val celular: String,
    var card: Card?
)
