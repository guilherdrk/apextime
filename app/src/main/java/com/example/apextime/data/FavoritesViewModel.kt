package com.example.apextime.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private val favoritesDataStore = FavoritesDataStore(application.applicationContext)

    val favoriteIdsState: StateFlow<Set<String>> = favoritesDataStore.favoriteIdsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = setOf("f1")
        )

    fun toggleFavorite(id: String) {
        viewModelScope.launch {
            favoritesDataStore.toggleFavorite(id)
        }
    }

    fun isFavorite(id: String): Boolean {
        return favoriteIdsState.value.contains(id)
    }
}
