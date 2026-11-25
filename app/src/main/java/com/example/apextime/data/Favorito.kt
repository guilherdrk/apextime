package com.example.apextime.data

data class CorridaFavorita(
    val id: String,
    val categoria: String,
    val nome: String,
    val circuito: String,
    val dataHoraFormatada: String,
    val anotacaoPessoal: String = "",
    val lembreteAtivo: Boolean = false
)
