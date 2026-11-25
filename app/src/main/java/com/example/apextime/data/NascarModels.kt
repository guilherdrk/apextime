package com.example.apextime.data

import com.google.gson.annotations.SerializedName

data class NascarRaceDto(
    @SerializedName("race_id") val raceId: Int,
    @SerializedName("series_id") val serieId: Int,
    @SerializedName("race_season") val temporada: Int,
    @SerializedName("race_name") val nome: String,
    @SerializedName("track_id") val trackId: Int,
    @SerializedName("track_name") val nomeCircuito: String,
    @SerializedName("race_date") val dataCorrida: String?,
    @SerializedName("qualifying_date") val dataClassificacao: String?,
    @SerializedName("tunein_date") val dataTransmissao: String?,
    @SerializedName("scheduled_distance") val distanciaProgramadaMilhas: Double?,
    @SerializedName("scheduled_laps") val voltasProgramadas: Int?,
    @SerializedName("actual_laps") val voltasReais: Int?
)
