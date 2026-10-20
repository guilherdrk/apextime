package com.example.apextime.data

/**
 * Representa uma corrida favoritada. Guardamos os dados já formatados (não só um ID)
 * porque a tela de Favoritos precisa exibir isso sem ter que buscar na API de novo.
 */
data class CorridaFavorita(
    val id: String,          // único por corrida, ex: "f1-interlagos-21" ou "nascar-5593"
    val categoria: String,   // "F1" ou "NASCAR"
    val nome: String,
    val circuito: String,
    val dataHoraFormatada: String
)