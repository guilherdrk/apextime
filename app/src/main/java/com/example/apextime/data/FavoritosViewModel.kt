package com.example.apextime.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritosViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FavoritoRepository(application)

    val favoritos: StateFlow<List<CorridaFavorita>> = repository.favoritos.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun alternarFavorito(corrida: CorridaFavorita) {
        viewModelScope.launch {
            val jaExiste = favoritos.value.any { it.id == corrida.id }
            if (jaExiste) {
                repository.remover(corrida.id)
            } else {
                repository.alternarFavorito(corrida)
            }
        }
    }

    fun salvarAnotacao(corrida: CorridaFavorita, novaAnotacao: String) {
        viewModelScope.launch {
            val atualizada = corrida.copy(anotacaoPessoal = novaAnotacao)
            repository.atualizar(atualizada)
        }
    }

    fun remover(id: String) {
        viewModelScope.launch {
            repository.remover(id)
        }
    }
}
