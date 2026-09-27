package com.example.apextime.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

object NascarSeries {
    const val CUP = 1
    const val XFINITY = 2
    const val TRUCKS = 3
}

interface NascarApiService {
    @GET("cacher/{ano}/{serieId}/race_list_basic.json")
    suspend fun getCalendario(
        @Path("ano") ano: Int,
        @Path("serieId") serieId: Int
    ): List<NascarRaceDto>
}

object NascarRetrofitInstance {
    private const val BASE_URL = "https://cf.nascar.com/"

    val api: NascarApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NascarApiService::class.java)
    }
}