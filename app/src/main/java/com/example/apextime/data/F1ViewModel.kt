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
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

data class SessaoUi(
    val tag: String,
    val nome: String,
    val horario: String,
    val descricao: String,
    val destaque: Boolean
)

data class HomeUiState(
    val carregando: Boolean = true,
    val erro: String? = null,
    val etapa: String = "",
    val nomeCorrida: String = "",
    val circuito: String = "",
    val localCircuito: String = "",
    val dataHoraFormatada: String = "",
    val dias: Int = 0,
    val horas: Int = 0,
    val minutos: Int = 0,
    val segundos: Int = 0,
    val sessoes: List<SessaoUi> = emptyList(),
    val voltas: Int? = null,
    val temperaturaTexto: String = "--°C",
    val condicaoPista: String = "Carregando...",
    val climaCarregando: Boolean = true,
    val recordeTempo: String = "--:--.---",
    val recordePiloto: String = "",
    val recordeAno: String = "",
    val recordeCarregando: Boolean = true
)

private val ZONA_BRASILIA = ZoneId.of("America/Sao_Paulo")

private val PAISES_PT = mapOf(
    "Brazil" to "Brasil",
    "United Kingdom" to "Reino Unido",
    "United States" to "EUA",
    "Italy" to "Itália",
    "Spain" to "Espanha",
    "Austria" to "Áustria",
    "Belgium" to "Bélgica",
    "Netherlands" to "Holanda",
    "Hungary" to "Hungria",
    "Azerbaijan" to "Azerbaijão",
    "Singapore" to "Singapura",
    "Mexico" to "México",
    "Qatar" to "Catar",
    "United Arab Emirates" to "Emirados Árabes",
    "Japan" to "Japão",
    "China" to "China",
    "Australia" to "Austrália",
    "Bahrain" to "Bahrein",
    "Saudi Arabia" to "Arábia Saudita",
    "Canada" to "Canadá",
    "Monaco" to "Mônaco"
)

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

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
                Log.d("HomeViewModel", "Buscando próxima corrida na API...")
                val resposta = RetrofitInstance.api.getNextRace()
                Log.d("HomeViewModel", "Resposta recebida: $resposta")
                val corrida = resposta.mrData.raceTable.races.firstOrNull()

                if (corrida == null) {
                    _uiState.update { it.copy(carregando = false, erro = "Nenhuma corrida futura encontrada") }
                    return@launch
                }

                val horaCorrida = corrida.time ?: "00:00:00Z"
                val instante = Instant.parse("${corrida.date}T$horaCorrida")
                instanteCorrida = instante

                val zonada = instante.atZone(ZONA_BRASILIA)
                val diaSemana = zonada.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("pt", "BR"))
                    .replaceFirstChar { it.uppercase() }
                val dataHoraFormatada = "$diaSemana às %02d:%02d (Brasília)".format(zonada.hour, zonada.minute)

                val sessoes = listOfNotNull(
                    corrida.firstPractice?.let { montarSessao("Treino Livre 1", "Fórmula 1 • 60 minutos de pista", it) },
                    corrida.secondPractice?.let { montarSessao("Treino Livre 2", "Fórmula 1 • Simulação de corrida", it) },
                    corrida.thirdPractice?.let { montarSessao("Treino Livre 3", "Fórmula 1 • Ajustes finais", it) },
                    corrida.sprint?.let { montarSessao("Sprint", "Fórmula 1 • Corrida curta", it) },
                    corrida.qualifying?.let { montarSessao("Classificação Oficial", "Fórmula 1 • Definição do grid", it) }
                ).sortedBy { it.first }

                _uiState.update {
                    it.copy(
                        carregando = false,
                        erro = null,
                        etapa = "ETAPA ${corrida.round}",
                        nomeCorrida = corrida.raceName,
                        circuito = corrida.circuit.circuitName,
                        localCircuito = "${corrida.circuit.location.locality}, ${
                            PAISES_PT[corrida.circuit.location.country] ?: corrida.circuit.location.country
                        }",
                        dataHoraFormatada = dataHoraFormatada,
                        sessoes = sessoes.map { par -> par.second }
                    )
                }

                iniciarContagemRegressiva()
                buscarClimaDaPista(
                    latitude = corrida.circuit.location.lat.toDouble(),
                    longitude = corrida.circuit.location.long.toDouble(),
                    circuitId = corrida.circuit.circuitId
                )
                buscarRecordeDaPista(corrida.circuit.circuitId)
            } catch (e: Exception) {
                // Olhe o Logcat filtrando por "HomeViewModel" para ver a causa real
                // (sem internet no emulador, timeout, erro de parsing do JSON, etc.)
                Log.e("HomeViewModel", "Falha ao buscar próxima corrida", e)
                _uiState.update {
                    it.copy(
                        carregando = false,
                        erro = "Não foi possível carregar os dados da corrida (${e.javaClass.simpleName}: ${e.message})"
                    )
                }
            }
        }
    }

    private fun montarSessao(nome: String, descricao: String, sessao: SessionDto): Pair<Instant, SessaoUi> {
        val instante = Instant.parse("${sessao.date}T${sessao.time}")
        val zonada = instante.atZone(ZONA_BRASILIA)
        val hoje = Instant.now().atZone(ZONA_BRASILIA).toLocalDate()
        val diaDaSessao = zonada.toLocalDate()

        val tag = when {
            diaDaSessao.isEqual(hoje) -> "EM BREVE • HOJE"
            else -> zonada.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("pt", "BR")).uppercase()
        }

        val horario = "%02d:%02d".format(zonada.hour, zonada.minute)

        return instante to SessaoUi(
            tag = tag,
            nome = nome,
            horario = horario,
            descricao = descricao,
            destaque = diaDaSessao.isEqual(hoje)
        )
    }

    private fun buscarClimaDaPista(latitude: Double, longitude: Double, circuitId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(climaCarregando = true) }
            try {
                Log.d("HomeViewModel", "Buscando clima em lat=$latitude, long=$longitude")
                val resposta = WeatherRetrofitInstance.api.getClimaAtual(latitude, longitude)
                val atual = resposta.current

                _uiState.update {
                    it.copy(
                        climaCarregando = false,
                        temperaturaTexto = "%.0f°C".format(atual.temperatura),
                        condicaoPista = condicaoDaPista(atual.precipitacaoMm, atual.codigoClima),
                        voltas = VoltasPorCircuito.voltasPara(circuitId)
                    )
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Falha ao buscar clima", e)
                _uiState.update {
                    it.copy(
                        climaCarregando = false,
                        temperaturaTexto = "--°C",
                        condicaoPista = "Clima indisponível",
                        voltas = VoltasPorCircuito.voltasPara(circuitId)
                    )
                }
            }
        }
    }

    private fun buscarRecordeDaPista(circuitId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(recordeCarregando = true) }
            try {
                Log.d("HomeViewModel", "Buscando recorde de volta em circuitId=$circuitId")
                val resposta = RetrofitInstance.api.getRecordesDoCircuito(circuitId)

                var melhorSegundos = Double.MAX_VALUE
                var melhorTempo = ""
                var melhorPiloto = ""
                var melhorAno = ""

                resposta.mrData.raceTable.races.forEach { race ->
                    val resultado = race.results.firstOrNull() ?: return@forEach
                    val segundos = tempoParaSegundos(resultado.fastestLap.time.time)
                    if (segundos < melhorSegundos) {
                        melhorSegundos = segundos
                        melhorTempo = resultado.fastestLap.time.time
                        melhorPiloto = "${resultado.driver.givenName.first()}. ${resultado.driver.familyName}"
                        melhorAno = race.season
                    }
                }

                if (melhorTempo.isEmpty()) {
                    Log.w("HomeViewModel", "Nenhum recorde encontrado para circuitId=$circuitId")
                    _uiState.update {
                        it.copy(recordeCarregando = false, recordeTempo = "--:--.---", recordePiloto = "Sem dados", recordeAno = "")
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            recordeCarregando = false,
                            recordeTempo = melhorTempo,
                            recordePiloto = melhorPiloto,
                            recordeAno = melhorAno
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Falha ao buscar recorde da pista", e)
                _uiState.update {
                    it.copy(recordeCarregando = false, recordeTempo = "--:--.---", recordePiloto = "Indisponível", recordeAno = "")
                }
            }
        }
    }

    /** Converte "1:10.540" (m:ss.mmm) em segundos totais, para comparar tempos. */
    private fun tempoParaSegundos(tempo: String): Double {
        val partes = tempo.split(":")
        return if (partes.size == 2) {
            partes[0].toDouble() * 60 + partes[1].toDouble()
        } else {
            partes[0].toDouble()
        }
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