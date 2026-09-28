package com.example.apextime.data

import com.google.gson.annotations.SerializedName

/**
 * Resposta de: https://cf.nascar.com/cacher/{ano}/{serieId}/race_list_basic.json
 * É um ARRAY direto (sem wrapper tipo "MRData"), com o calendário inteiro daquele ano/série
 * (corridas passadas e futuras misturadas).
 *
 * IDs de série conhecidos: 1 = Cup, 2 = Xfinity, 3 = Trucks.
 *
 * Atenção: essa API não é documentada oficialmente pela NASCAR — é o mesmo feed que o
 * site nascar.com usa internamente. Pode mudar de formato sem aviso.
 */
data class NascarRaceDto(
    @SerializedName("race_id") val raceId: Int,
    @SerializedName("series_id") val serieId: Int,
    @SerializedName("race_season") val temporada: Int,
    @SerializedName("race_name") val nome: String,
    @SerializedName("track_id") val trackId: Int,
    @SerializedName("track_name") val nomeCircuito: String,
    // Sem sufixo "_utc" no JSON — assumindo horário local dos EUA (America/New_York).
    @SerializedName("race_date") val dataCorrida: String?,
    @SerializedName("qualifying_date") val dataClassificacao: String?,
    @SerializedName("tunein_date") val dataTransmissao: String?,
    @SerializedName("scheduled_distance") val distanciaProgramadaMilhas: Double?,
    @SerializedName("scheduled_laps") val voltasProgramadas: Int?,
    @SerializedName("actual_laps") val voltasReais: Int?
)