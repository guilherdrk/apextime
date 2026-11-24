package com.example.apextime.data

import com.example.apextime.R
import java.time.Instant

data class WecStage(
    val roundCode: String,
    val name: String,
    val location: String,
    val dateRange: String,
    val duration: String,
    val targetInstant: Instant,
    val latitude: Double,
    val longitude: Double
)

object WecData {
    val schedule2027 = listOf(
        WecStage(
            roundCode = "R01",
            name = "Qatar 1812 KM",
            location = "LUSAIL INTERNATIONAL CIRCUIT • QATAR",
            dateRange = "27 Mar 2027",
            duration = "1812 KM (10 HORAS)",
            targetInstant = Instant.parse("2027-03-27T08:00:00Z"),
            latitude = 25.4900,
            longitude = 51.4542
        ),
        WecStage(
            roundCode = "R02",
            name = "6 Hours of Imola",
            location = "AUTODROMO ENZO E DINO FERRARI • ITÁLIA",
            dateRange = "11 Apr 2027",
            duration = "6 HORAS DE DURANÇÃO",
            targetInstant = Instant.parse("2027-04-11T11:00:00Z"),
            latitude = 44.3439,
            longitude = 11.7167
        ),
        WecStage(
            roundCode = "R03",
            name = "6 Hours of Silverstone",
            location = "SILVERSTONE CIRCUIT • REINO UNIDO",
            dateRange = "25 Apr 2027",
            duration = "6 HORAS DE DURANÇÃO",
            targetInstant = Instant.parse("2027-04-25T11:00:00Z"),
            latitude = 52.0786,
            longitude = -1.0169
        ),
        WecStage(
            roundCode = "R04",
            name = "TotalEnergies 6 Hours of Spa-Francorchamps",
            location = "CIRCUIT DE SPA-FRANCORCHAMPS • BÉLGICA",
            dateRange = "15 May 2027",
            duration = "6 HORAS DE SPA",
            targetInstant = Instant.parse("2027-05-15T11:00:00Z"),
            latitude = 50.4372,
            longitude = 5.9714
        ),
        WecStage(
            roundCode = "R05",
            name = "93rd 24 Hours of Le Mans",
            location = "CIRCUIT DE LA SARTHE • FRANÇA",
            dateRange = "12 Jun 2027",
            duration = "24 HORAS DE LE MANS",
            targetInstant = Instant.parse("2027-06-12T14:00:00Z"),
            latitude = 47.9500,
            longitude = 0.2242
        ),
        WecStage(
            roundCode = "R06",
            name = "Rolex 6 Hours of São Paulo",
            location = "AUTÓDROMO DE INTERLAGOS • BRASIL",
            dateRange = "11 Jul 2027",
            duration = "6 HORAS DE SÃO PAULO",
            targetInstant = Instant.parse("2027-07-11T14:00:00Z"),
            latitude = -23.7036,
            longitude = -46.6997
        )
    )

    fun getUpcomingStage(): WecStage {
        val now = Instant.now()
        return schedule2027.firstOrNull { it.targetInstant.isAfter(now) } ?: schedule2027.first()
    }

    fun getNextStage(): CategorySeries {
        val next = getUpcomingStage()
        return CategorySeries(
            id = "wec",
            name = "FIA WEC",
            division = "HYPERCAR & LMGT3 MULTI-CLASS",
            statusTag = next.duration,
            isLiveOrActive = false,
            bannerImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e0/2023_24_Hours_of_Le_Mans_-_Ferrari_499P_%2853001889410%29.jpg/1024px-2023_24_Hours_of_Le_Mans_-_Ferrari_499P_%2853001889410%29.jpg",
            bannerDrawableRes = R.drawable.banner_wec,
            bannerLocationTag = next.location,
            metaTags = listOf("9 ETAPAS", "TEMPORADA 2027", "DRIVER RATING GOLD"),
            nextStage = NextStageInfo(
                roundCode = next.roundCode,
                title = next.name,
                dateRange = "(${next.dateRange})"
            )
        )
    }
}
