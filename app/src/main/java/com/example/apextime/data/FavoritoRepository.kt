package com.example.apextime.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.favoritosDataStore by preferencesDataStore(name = "favoritos")
private val CHAVE_FAVORITOS = stringPreferencesKey("lista_favoritos_json")

/**
 * Guarda a lista de corridas favoritadas em disco (DataStore), serializada como JSON.
 * Sobrevive a fechar o app — diferente de guardar só em memória no ViewModel.
 */
class FavoritosRepository(private val context: Context) {
    private val gson = Gson()
    private val tipoLista = object : TypeToken<List<CorridaFavorita>>() {}.type

    val favoritos: Flow<List<CorridaFavorita>> = context.favoritosDataStore.data.map { preferencias ->
        lerLista(preferencias[CHAVE_FAVORITOS])
    }

    suspend fun alternarFavorito(corrida: CorridaFavorita) {
        context.favoritosDataStore.edit { preferencias ->
            val listaAtual = lerLista(preferencias[CHAVE_FAVORITOS]).toMutableList()
            val jaExiste = listaAtual.any { it.id == corrida.id }

            if (jaExiste) {
                listaAtual.removeAll { it.id == corrida.id }
            } else {
                listaAtual.add(corrida)
            }

            preferencias[CHAVE_FAVORITOS] = gson.toJson(listaAtual)
        }
    }

    suspend fun remover(id: String) {
        context.favoritosDataStore.edit { preferencias ->
            val listaAtual = lerLista(preferencias[CHAVE_FAVORITOS]).toMutableList()
            listaAtual.removeAll { it.id == id }
            preferencias[CHAVE_FAVORITOS] = gson.toJson(listaAtual)
        }
    }

    private fun lerLista(json: String?): List<CorridaFavorita> {
        if (json.isNullOrBlank()) return emptyList()
        return try {
            gson.fromJson<List<CorridaFavorita>>(json, tipoLista) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}