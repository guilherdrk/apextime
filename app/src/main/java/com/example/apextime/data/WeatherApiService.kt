package com.example.apextime.data

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/* API pública Open-Meteo: https://open-meteo.com — sem chave, sem cadastro */

data class OpenMeteoResponse(
    @SerializedName("current") val current: CurrentWeatherDto
)

data class CurrentWeatherDto(
    @SerializedName("temperature_2m") val temperatura: Double,
    @SerializedName("precipitation") val precipitacaoMm: Double,
    @SerializedName("weather_code") val codigoClima: Int
)

interface WeatherApiService {
    @GET("v1/forecast")
    suspend fun getClimaAtual(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") parametros: String = "temperature_2m,precipitation,weather_code"
    ): OpenMeteoResponse
}

object WeatherRetrofitInstance {
    private const val BASE_URL = "https://api.open-meteo.com/"

    val api: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }
}

/**
 * Códigos WMO (weather_code) que indicam chuva/tempestade.
 * https://open-meteo.com/en/docs -> tabela "WMO Weather interpretation codes"
 */
private val CODIGOS_DE_CHUVA = setOf(51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 80, 81, 82, 95, 96, 99)

fun condicaoDaPista(precipitacaoMm: Double, codigoClima: Int): String {
    val chovendoAgora = precipitacaoMm > 0.0 || codigoClima in CODIGOS_DE_CHUVA
    return if (chovendoAgora) "Pista Molhada" else "Pista Seca"
}