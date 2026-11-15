package com.example.apextime.data

/**
 * Tabela estática de voltas por circuito na Fórmula 1.
 */
object VoltasPorCircuito {

    private val voltas = mapOf(
        "albert_park" to 58,     // Austrália
        "shanghai" to 56,        // China
        "suzuka" to 53,          // Japão
        "bahrain" to 57,         // Bahrein
        "jeddah" to 50,          // Arábia Saudita
        "miami" to 57,           // Miami
        "imola" to 63,           // Emilia Romagna
        "monaco" to 78,          // Mônaco
        "villeneuve" to 70,      // Canadá
        "catalunya" to 66,       // Espanha
        "red_bull_ring" to 71,   // Áustria
        "silverstone" to 52,     // Grã-Bretanha
        "hungaroring" to 70,     // Hungria
        "spa" to 44,             // Bélgica
        "zandvoort" to 72,       // Holanda
        "monza" to 53,           // Itália
        "baku" to 51,            // Azerbaijão
        "marina_bay" to 62,      // Singapura
        "americas" to 56,        // EUA (COTA)
        "rodriguez" to 71,       // México
        "interlagos" to 71,      // Brasil
        "las_vegas" to 50,       // Las Vegas
        "vegas" to 50,           // Las Vegas (variação)
        "losail" to 57,          // Catar
        "yas_marina" to 58       // Abu Dhabi
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
