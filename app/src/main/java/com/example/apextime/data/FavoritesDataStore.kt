package com.example.apextime.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "apextime_favorites")

class FavoritesDataStore(private val context: Context) {

    companion object {
        private val FAVORITE_IDS_KEY = stringSetPreferencesKey("favorite_category_ids")
    }

    val favoriteIdsFlow: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[FAVORITE_IDS_KEY] ?: setOf("f1") // F1 favoritada por padrão
    }

    suspend fun toggleFavorite(id: String) {
        context.dataStore.edit { preferences ->
            val current = preferences[FAVORITE_IDS_KEY] ?: setOf("f1")
            val updated = if (current.contains(id)) {
                current - id
            } else {
                current + id
            }
            preferences[FAVORITE_IDS_KEY] = updated
        }
    }
}
