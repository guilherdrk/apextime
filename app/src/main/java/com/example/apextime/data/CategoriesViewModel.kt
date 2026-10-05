package com.example.apextime.data

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apextime.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CategoriesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriesUiState(isLoading = true))
    val uiState: StateFlow<CategoriesUiState> = _uiState.asStateFlow()

    init {
        loadCategoriesData()
    }

    fun onFilterSelected(filter: CategoryFilterTab) {
        _uiState.update { currentState ->
            currentState.copy(activeFilter = filter)
        }
    }

    fun refreshData() {
        _uiState.update { it.copy(isLoading = true) }
        loadCategoriesData()
    }

    private fun loadCategoriesData() {
        viewModelScope.launch {
            val f1Series = fetchF1Data()
            val nascarSeries = fetchNascarData()
            val indyCarSeries = IndyCarData.getNextStage()
            val wecSeries = WecData.getNextStage()

            val allCategories = listOf(f1Series, indyCarSeries, wecSeries, nascarSeries)

            _uiState.update {
                it.copy(
                    categories = allCategories,
                    totalHomologatedSeries = allCategories.size,
                    isLoading = false
                )
            }
        }
    }

    private suspend fun fetchF1Data(): CategorySeries {
        val f1BannerUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/30/2022_Austrian_Grand_Prix_%2852210878144%29.jpg/1024px-2022_Austrian_Grand_Prix_%2852210878144%29.jpg"
        return try {
            val response = RetrofitInstance.api.getNextRace()
            val race = response.mrData.raceTable.races.firstOrNull()

            if (race != null) {
                val roundCode = "R${race.round}"
                val raceTitle = race.raceName
                val dateStr = race.date

                CategorySeries(
                    id = "f1",
                    name = "Fórmula 1 World Championship",
                    division = "FIA OPEN-WHEEL DIVISION",
                    statusTag = "EM ANDAMENTO",
                    isLiveOrActive = true,
                    bannerImageUrl = f1BannerUrl,
                    bannerDrawableRes = R.drawable.banner_f1,
                    bannerLocationTag = "${race.circuit.circuitName.uppercase()} • ${race.circuit.location.locality.uppercase()}",
                    metaTags = listOf("24 ETAPAS", "TEMPORADA ${race.season}", "FIA SUPER LICENSE A"),
                    nextStage = NextStageInfo(
                        roundCode = roundCode,
                        title = raceTitle,
                        dateRange = "($dateStr)"
                    )
                )
            } else {
                defaultF1Series()
            }
        } catch (e: Exception) {
            Log.e("CategoriesViewModel", "Erro ao buscar F1 da API", e)
            defaultF1Series()
        }
    }

    private suspend fun fetchNascarData(): CategorySeries {
        val nascarBannerUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a2/2022_Daytona_500_start.jpg/1024px-2022_Daytona_500_start.jpg"
        return try {
            val anoAtual = 2026
            val races = NascarRetrofitInstance.api.getCalendario(anoAtual, NascarSeries.CUP)
            val proximaCorrida = races.firstOrNull()

            if (proximaCorrida != null) {
                val roundCode = "R01"
                val raceName = proximaCorrida.nome.ifEmpty { "DAYTONA 500" }
                val trackName = proximaCorrida.nomeCircuito.ifEmpty { "DAYTONA INTERNATIONAL SPEEDWAY" }

                CategorySeries(
                    id = "nascar",
                    name = "NASCAR Cup Series",
                    division = "NEXT-GEN OVAL DIVISION",
                    statusTag = raceName,
                    isLiveOrActive = false,
                    bannerImageUrl = nascarBannerUrl,
                    bannerDrawableRes = R.drawable.banner_nascar,
                    bannerLocationTag = trackName.uppercase(),
                    metaTags = listOf("${races.size.ifZero(36)} ETAPAS", "PLAYOFFS FORMAT", "CLASS C OVAL"),
                    nextStage = NextStageInfo(
                        roundCode = roundCode,
                        title = raceName,
                        dateRange = "(${proximaCorrida.dataCorrida ?: "Abertura"})"
                    )
                )
            } else {
                defaultNascarSeries()
            }
        } catch (e: Exception) {
            Log.e("CategoriesViewModel", "Erro ao buscar NASCAR da API", e)
            defaultNascarSeries()
        }
    }

    private fun Int.ifZero(default: Int) = if (this == 0) default else this

    private fun defaultF1Series() = CategorySeries(
        id = "f1",
        name = "Fórmula 1 World Championship",
        division = "FIA OPEN-WHEEL DIVISION",
        statusTag = "EM ANDAMENTO",
        isLiveOrActive = true,
        bannerImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/30/2022_Austrian_Grand_Prix_%2852210878144%29.jpg/1024px-2022_Austrian_Grand_Prix_%2852210878144%29.jpg",
        bannerDrawableRes = R.drawable.banner_f1,
        bannerLocationTag = "AUTÓDROMO DE INTERLAGOS",
        metaTags = listOf("24 ETAPAS", "TEMPORADA 2026", "FIA SUPER LICENSE A"),
        nextStage = NextStageInfo(
            roundCode = "R21",
            title = "GP de São Paulo",
            dateRange = "(01 a 03 Nov)"
        )
    )

    private fun defaultNascarSeries() = CategorySeries(
        id = "nascar",
        name = "NASCAR Cup Series",
        division = "NEXT-GEN OVAL DIVISION",
        statusTag = "DAYTONA 500",
        isLiveOrActive = false,
        bannerImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a2/2022_Daytona_500_start.jpg/1024px-2022_Daytona_500_start.jpg",
        bannerDrawableRes = R.drawable.banner_nascar,
        bannerLocationTag = "SUPERSPEEDWAY BANKING",
        metaTags = listOf("36 ETAPAS", "PLAYOFFS FORMAT", "CLASS C OVAL"),
        nextStage = NextStageInfo(
            roundCode = "R01",
            title = "Daytona 500 (The Great American Race)",
            dateRange = "(Abertura de Temporada)"
        )
    )
}
