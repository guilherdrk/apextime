package com.example.apextime.data

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.Year
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

data class NascarUiState(
    val carregando: Boolean = true,
    val erro: String? = null,
    val nomeCorrida: String = "",
    val circuito: String = "",
    val distanciaTexto: String = "--",
    val voltas: Int? = null,
    val dataHoraFormatada: String = "",
    val dias: Int = 0,
    val horas: Int = 0,
    val minutos: Int = 0,
    val segundos: Int = 0
)

// Horário local presumido dos dados da API (ver observação no NascarModels.kt)
private val ZONA_CORRIDA_EUA = ZoneId.of("America/New_York")
private val ZONA_BRASILIA_NASCAR = ZoneId.of("America/Sao_Paulo")
private val FORMATO_DATA_NASCAR = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

class NascarViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NascarUiState())
    val uiState: StateFlow<NascarUiState> = _uiState.asStateFlow()

    private var instanteCorrida: Instant? = null

    init {
        carregarProximaCorrida()
    }

    fun tentarNovamente() {
        _uiState.update { it.copy(carregando = true, erro = null) }
        carregarProximaCorrida()
    }

    private fun carregarProximaCorrida() {
        viewModelScope.launch {
            try {
                val anoAtual = Year.now().value
                Log.d("NascarViewModel", "Buscando calendário NASCAR $anoAtual...")

                var proxima = encontrarProximaCorrida(
                    NascarRetrofitInstance.api.getCalendario(anoAtual, NascarSeries.CUP)
                )

                // Se a temporada atual já acabou, tenta o calendário do ano seguinte
                if (proxima == null) {
                    Log.d("NascarViewModel", "Sem corrida futura em $anoAtual, tentando ${anoAtual + 1}")
                    proxima = encontrarProximaCorrida(
                        NascarRetrofitInstance.api.getCalendario(anoAtual + 1, NascarSeries.CUP)
                    )
                }

                if (proxima == null || proxima.dataCorrida == null) {
                    _uiState.update { it.copy(carregando = false, erro = "Nenhuma corrida futura encontrada") }
                    return@launch
                }

                val dataHoraLocal = LocalDateTime.parse(proxima.dataCorrida, FORMATO_DATA_NASCAR)
                val instante = dataHoraLocal.atZone(ZONA_CORRIDA_EUA).toInstant()
                instanteCorrida = instante

                val zonadaBrasilia = instante.atZone(ZONA_BRASILIA_NASCAR)
                val diaSemana = zonadaBrasilia.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("pt", "BR"))
                    .replaceFirstChar { it.uppercase() }
                val dataHoraFormatada = "$diaSemana às %02d:%02d (Brasília)".format(zonadaBrasilia.hour, zonadaBrasilia.minute)

                _uiState.update {
                    it.copy(
                        carregando = false,
                        erro = null,
                        nomeCorrida = proxima.nome,
                        circuito = proxima.nomeCircuito,
                        voltas = proxima.voltasProgramadas,
                        distanciaTexto = proxima.distanciaProgramadaMilhas?.let { d -> "%.1f milhas".format(d) } ?: "--",
                        dataHoraFormatada = dataHoraFormatada
                    )
                }

                iniciarContagemRegressiva()
            } catch (e: Exception) {
                Log.e("NascarViewModel", "Falha ao buscar calendário NASCAR", e)
                _uiState.update {
                    it.copy(
                        carregando = false,
                        erro = "Não foi possível carregar os dados da NASCAR (${e.javaClass.simpleName}: ${e.message})"
                    )
                }
            }
        }
    }

    private fun encontrarProximaCorrida(calendario: List<NascarRaceDto>): NascarRaceDto? {
        val agora = LocalDateTime.now(ZONA_CORRIDA_EUA)
        return calendario
            .mapNotNull { corrida ->
                val dataTexto = corrida.dataCorrida ?: return@mapNotNull null
                val dataHora = try {
                    LocalDateTime.parse(dataTexto, FORMATO_DATA_NASCAR)
                } catch (e: Exception) {
                    null
                } ?: return@mapNotNull null
                corrida to dataHora
            }
            .filter { (_, dataHora) -> dataHora.isAfter(agora) }
            .minByOrNull { (_, dataHora) -> dataHora }
            ?.first
    }

    private fun iniciarContagemRegressiva() {
        viewModelScope.launch {
            while (true) {
                val alvo = instanteCorrida ?: break
                val restante = Duration.between(Instant.now(), alvo)

                if (restante.isNegative) {
                    _uiState.update { it.copy(dias = 0, horas = 0, minutos = 0, segundos = 0) }
                    break
                }

                _uiState.update {
                    it.copy(
                        dias = restante.toDays().toInt(),
                        horas = (restante.toHours() % 24).toInt(),
                        minutos = (restante.toMinutes() % 60).toInt(),
                        segundos = (restante.seconds % 60).toInt()
                    )
                }
                delay(1000)
            }
        }
    }
}