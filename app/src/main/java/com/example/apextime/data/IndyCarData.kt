package com.example.apextime.data

import com.example.apextime.R

data class IndyCarStage(
    val roundCode: String,
    val name: String,
    val location: String,
    val dateRange: String,
    val isStreetOrOval: String,
    val latitude: Double,
    val longitude: Double
)

object IndyCarData {
    val schedule2026 = listOf(
        IndyCarStage(
            roundCode = "R01",
            name = "Firestone Grand Prix of St. Petersburg",
            location = "ST. PETERSBURG • STREET CIRCUIT",
            dateRange = "01 a 03 Mar",
            isStreetOrOval = "RUA • 1.8 MILHAS",
            latitude = 27.7661,
            longitude = -82.6314
        ),
        IndyCarStage(
            roundCode = "R02",
            name = "The Thermal Club $1 Million Challenge",
            location = "THERMAL • CLUB CIRCUIT",
            dateRange = "22 a 24 Mar",
            isStreetOrOval = "MISTO • 3.0 MILHAS",
            latitude = 33.6190,
            longitude = -116.1438
        ),
        IndyCarStage(
            roundCode = "R03",
            name = "Acura Grand Prix of Long Beach",
            location = "CALIFORNIA • STREET CIRCUIT",
            dateRange = "19 a 21 Abr",
            isStreetOrOval = "RUA • 1.9 MILHAS",
            latitude = 33.7651,
            longitude = -118.1892
        ),
        IndyCarStage(
            roundCode = "R04",
            name = "Children's of Alabama Indy Grand Prix",
            location = "BARBER MOTORSPORTS PARK",
            dateRange = "26 a 28 Abr",
            isStreetOrOval = "MISTO • 2.3 MILHAS",
            latitude = 33.5323,
            longitude = -86.6186
        ),
        IndyCarStage(
            roundCode = "R05",
            name = "108th Running of the Indianapolis 500",
            location = "INDIANAPOLIS MOTOR SPEEDWAY",
            dateRange = "24 a 26 Mai",
            isStreetOrOval = "OVAL SUPERSPEEDWAY • 2.5 MILHAS",
            latitude = 39.7950,
            longitude = -86.2347
        )
    )

    fun getNextStage(): CategorySeries {
        val next = schedule2026[2] // Acura GP of Long Beach
        return CategorySeries(
            id = "indycar",
            name = "NTT IndyCar Series",
            division = "NORTH AMERICAN OPEN-WHEEL",
            statusTag = "PRÓXIMA SEMANA",
            isLiveOrActive = false,
            bannerImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/da/2021_Indy_500_start.jpg/1024px-2021_Indy_500_start.jpg",
            bannerDrawableRes = R.drawable.banner_indycar,
            bannerLocationTag = next.location,
            metaTags = listOf("17 ETAPAS", "OVAIS & MISTOS", "CLASS B 3.00+"),
            nextStage = NextStageInfo(
                roundCode = next.roundCode,
                title = next.name,
                dateRange = "(${next.dateRange})"
            )
        )
    }
}
