package com.example.apextime.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * AndroidViewModel (em vez de ViewModel comum) porque o DataStore precisa de um Context,
 * e o Application é o Context seguro de guardar aqui dentro (não vaza memória de Activity).
 */
class FavoritosViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = FavoritosRepository(application)

    val favoritos: StateFlow<List<CorridaFavorita>> = repositorio.favoritos.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun alternarFavorito(corrida: CorridaFavorita) {
        viewModelScope.launch {
            repositorio.alternarFavorito(corrida)
        }
    }

    fun remover(id: String) {
        viewModelScope.launch {
            repositorio.remover(id)
        }
    }
}