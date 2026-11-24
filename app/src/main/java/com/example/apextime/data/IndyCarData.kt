package com.example.apextime.data

import com.example.apextime.R
import java.time.Instant

data class IndyCarStage(
    val roundCode: String,
    val name: String,
    val location: String,
    val dateRange: String,
    val isStreetOrOval: String,
    val targetInstant: Instant,
    val latitude: Double,
    val longitude: Double
)

object IndyCarData {
    val schedule2027 = listOf(
        IndyCarStage(
            roundCode = "R01",
            name = "Firestone Grand Prix of St. Petersburg",
            location = "ST. PETERSBURG • STREET CIRCUIT",
            dateRange = "02 Mar 2027",
            isStreetOrOval = "RUA • 1.8 MILHAS",
            targetInstant = Instant.parse("2027-03-02T16:00:00Z"),
            latitude = 27.7661,
            longitude = -82.6314
        ),
        IndyCarStage(
            roundCode = "R02",
            name = "The Thermal Club $1 Million Challenge",
            location = "THERMAL • CLUB CIRCUIT",
            dateRange = "23 Mar 2027",
            isStreetOrOval = "MISTO • 3.0 MILHAS",
            targetInstant = Instant.parse("2027-03-23T18:00:00Z"),
            latitude = 33.6190,
            longitude = -116.1438
        ),
        IndyCarStage(
            roundCode = "R03",
            name = "Acura Grand Prix of Long Beach",
            location = "CALIFORNIA • STREET CIRCUIT",
            dateRange = "20 Abr 2027",
            isStreetOrOval = "RUA • 1.9 MILHAS",
            targetInstant = Instant.parse("2027-04-20T19:30:00Z"),
            latitude = 33.7651,
            longitude = -118.1892
        ),
        IndyCarStage(
            roundCode = "R04",
            name = "109th Running of the Indianapolis 500",
            location = "INDIANAPOLIS MOTOR SPEEDWAY",
            dateRange = "25 Mai 2027",
            isStreetOrOval = "OVAL SUPERSPEEDWAY • 2.5 MILHAS",
            targetInstant = Instant.parse("2027-05-25T16:30:00Z"),
            latitude = 39.7950,
            longitude = -86.2347
        )
    )

    fun getUpcomingStage(): IndyCarStage {
        val now = Instant.now()
        return schedule2027.firstOrNull { it.targetInstant.isAfter(now) } ?: schedule2027.first()
    }

    fun getNextStage(): CategorySeries {
        val next = getUpcomingStage()
        return CategorySeries(
            id = "indycar",
            name = "IndyCar Series",
            division = "NORTH AMERICAN OPEN-WHEEL",
            statusTag = "TEMPORADA 2027",
            isLiveOrActive = false,
            bannerImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/da/2021_Indy_500_start.jpg/1024px-2021_Indy_500_start.jpg",
            bannerDrawableRes = R.drawable.banner_indycar,
            bannerLocationTag = next.location,
            metaTags = listOf("17 ETAPAS", "TEMPORADA 2027", "CLASS B 3.00+"),
            nextStage = NextStageInfo(
                roundCode = next.roundCode,
                title = next.name,
                dateRange = "(${next.dateRange})"
            )
        )
    }
}
