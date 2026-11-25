package com.example.apextime.data

object VoltasPorCircuito {

    private val voltas = mapOf(
        "albert_park" to 58,
        "shanghai" to 56,
        "suzuka" to 53,
        "bahrain" to 57,
        "jeddah" to 50,
        "miami" to 57,
        "imola" to 63,
        "monaco" to 78,
        "villeneuve" to 70,
        "catalunya" to 66,
        "red_bull_ring" to 71,
        "silverstone" to 52,
        "hungaroring" to 70,
        "spa" to 44,
        "zandvoort" to 72,
        "monza" to 53,
        "baku" to 51,
        "marina_bay" to 62,
        "americas" to 56,
        "rodriguez" to 71,
        "interlagos" to 71,
        "las_vegas" to 50,
        "vegas" to 50,
        "losail" to 57,
        "yas_marina" to 58
    )

    fun voltasPara(circuitId: String): Int {
        val key = circuitId.lowercase().trim()
        return voltas[key] ?: when {
            key.contains("vegas") -> 50
            key.contains("marina") || key.contains("singapore") -> 62
            key.contains("interlagos") || key.contains("sao_paulo") || key.contains("brazil") -> 71
            key.contains("monaco") -> 78
            key.contains("spa") -> 44
            key.contains("monza") -> 53
            key.contains("silverstone") -> 52
            else -> 57
        }
    }
}
