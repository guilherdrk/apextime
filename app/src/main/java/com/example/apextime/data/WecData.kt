package com.example.apextime.data

import com.example.apextime.R

data class WecStage(
    val roundCode: String,
    val name: String,
    val location: String,
    val dateRange: String,
    val duration: String,
    val latitude: Double,
    val longitude: Double
)

object WecData {
    val schedule2026 = listOf(
        WecStage(
            roundCode = "R01",
            name = "Qatar 1812 KM",
            location = "LUSAIL INTERNATIONAL CIRCUIT",
            dateRange = "26 a 28 Fev",
            duration = "1812 KM (10 HORAS)",
            latitude = 25.4900,
            longitude = 51.4542
        ),
        WecStage(
            roundCode = "R02",
            name = "6 Hours of Imola",
            location = "AUTODROMO ENZO E DINO FERRARI",
            dateRange = "19 a 21 Abr",
            duration = "6 HORAS DE DURANÇÃO",
            latitude = 44.3439,
            longitude = 11.7167
        ),
        WecStage(
            roundCode = "R03",
            name = "TotalEnergies 6 Hours of Spa-Francorchamps",
            location = "CIRCUIT DE SPA-FRANCORCHAMPS",
            dateRange = "09 a 11 Mai",
            duration = "6 HORAS DE SPA",
            latitude = 50.4372,
            longitude = 5.9714
        ),
        WecStage(
            roundCode = "R04",
            name = "92nd 24 Hours of Le Mans",
            location = "CIRCUIT DE LA SARTHE • LE MANS",
            dateRange = "12 a 16 Jun",
            duration = "24 HORAS DE LE MANS",
            latitude = 47.9500,
            longitude = 0.2242
        ),
        WecStage(
            roundCode = "R05",
            name = "Rolex 6 Hours of São Paulo",
            location = "AUTÓDROMO DE INTERLAGOS • BRASIL",
            dateRange = "12 a 14 Jul",
            duration = "6 HORAS DE SÃO PAULO",
            latitude = -23.7036,
            longitude = -46.6997
        )
    )

    fun getNextStage(): CategorySeries {
        val next = schedule2026[2] // 6 Hours of Spa-Francorchamps
        return CategorySeries(
            id = "wec",
            name = "FIA WEC (Mundial de Endurance)",
            division = "HYPERCAR & LMGT3 MULTI-CLASS",
            statusTag = next.duration,
            isLiveOrActive = false,
            bannerImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e0/2023_24_Hours_of_Le_Mans_-_Ferrari_499P_%2853001889410%29.jpg/1024px-2023_24_Hours_of_Le_Mans_-_Ferrari_499P_%2853001889410%29.jpg",
            bannerDrawableRes = R.drawable.banner_wec,
            bannerLocationTag = next.location,
            metaTags = listOf("8 ETAPAS", "ENDURANCE SERIES", "DRIVER RATING GOLD"),
            nextStage = NextStageInfo(
                roundCode = next.roundCode,
                title = next.name,
                dateRange = "(${next.dateRange})"
            )
        )
    }
}
