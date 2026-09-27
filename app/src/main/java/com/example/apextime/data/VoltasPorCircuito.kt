package com.example.apextime.data

/**
 * O número de voltas de cada GP é fixo por circuito (definido pelo regulamento da FIA,
 * que exige a menor quantidade de voltas que ultrapasse ~305 km, com exceção de Mônaco).
 * Não existe uma API pública que forneça isso pronto, então mantemos uma tabela estática
 * usando o mesmo "circuitId" retornado pela API Jolpica-F1/Ergast.
 *
 * Atualize aqui se a FIA mudar o traçado de algum circuito ou o calendário mudar de pista.
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
        "losail" to 57,          // Catar
        "yas_marina" to 58       // Abu Dhabi
    )

    fun voltasPara(circuitId: String): Int? = voltas[circuitId]
}