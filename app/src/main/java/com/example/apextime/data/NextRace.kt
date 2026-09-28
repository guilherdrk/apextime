package com.example.apextime.data

import com.google.gson.annotations.SerializedName

/* Schema compatível com a API Ergast/Jolpica-F1: https://api.jolpi.ca/ergast/f1/current/next.json */

data class NextRaceResponse(
    @SerializedName("MRData") val mrData: MrData
)

data class MrData(
    @SerializedName("RaceTable") val raceTable: RaceTable
)

data class RaceTable(
    @SerializedName("Races") val races: List<RaceDto>
)

data class RaceDto(
    val season: String,
    val round: String,
    val raceName: String,
    @SerializedName("Circuit") val circuit: CircuitDto,
    val date: String,
    val time: String?,
    @SerializedName("FirstPractice") val firstPractice: SessionDto?,
    @SerializedName("SecondPractice") val secondPractice: SessionDto?,
    @SerializedName("ThirdPractice") val thirdPractice: SessionDto?,
    @SerializedName("Qualifying") val qualifying: SessionDto?,
    @SerializedName("Sprint") val sprint: SessionDto?
)

data class CircuitDto(
    val circuitId: String,
    val circuitName: String,
    @SerializedName("Location") val location: LocationDto
)

data class LocationDto(
    val lat: String,
    val long: String,
    val locality: String,
    val country: String
)

data class SessionDto(
    val date: String,
    val time: String
)