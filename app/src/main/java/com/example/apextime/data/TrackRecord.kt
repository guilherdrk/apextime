package com.example.apextime.data

import com.google.gson.annotations.SerializedName

/* Resposta de: /circuits/{circuitId}/fastest/1/results.json
   Retorna, para cada edição do GP naquele circuito, quem fez a volta mais rápida (rank 1). */

data class FastestLapResponse(
    @SerializedName("MRData") val mrData: FastestLapMrData
)

data class FastestLapMrData(
    @SerializedName("RaceTable") val raceTable: FastestLapRaceTable
)

data class FastestLapRaceTable(
    @SerializedName("Races") val races: List<FastestLapRaceDto>
)

data class FastestLapRaceDto(
    val season: String,
    @SerializedName("Results") val results: List<FastestLapResultDto>
)

data class FastestLapResultDto(
    @SerializedName("Driver") val driver: DriverDto,
    @SerializedName("FastestLap") val fastestLap: FastestLapDto
)

data class DriverDto(
    val givenName: String,
    val familyName: String
)

data class FastestLapDto(
    val rank: String,
    val lap: String,
    @SerializedName("Time") val time: FastestLapTimeDto
)

data class FastestLapTimeDto(
    val time: String // ex: "1:10.540"
)