package com.example.apextime.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ErgastApiService {
    @GET("current/next.json")
    suspend fun getNextRace(): NextRaceResponse

    @GET("circuits/{circuitId}/fastest/1/results.json")
    suspend fun getRecordesDoCircuito(
        @Path("circuitId") circuitId: String,
        @Query("limit") limite: Int = 200
    ): FastestLapResponse
}

object RetrofitInstance {
    private const val BASE_URL = "https://api.jolpi.ca/ergast/f1/"

    val api: ErgastApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ErgastApiService::class.java)
    }
}
