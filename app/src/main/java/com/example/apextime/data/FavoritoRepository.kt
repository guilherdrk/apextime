package com.example.apextime.data

import android.content.Context
import com.example.apextime.data.local.AppDatabase
import com.example.apextime.data.local.CorridaFavoritaEntity
import com.example.apextime.data.local.FavoritoDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class FavoritoRepository(private val dao: FavoritoDao) {

    constructor(context: Context) : this(AppDatabase.getDatabase(context).favoritoDao())

    val favoritos: Flow<List<CorridaFavorita>> = dao.listarTodos().map { entities ->
        entities.map { it.toModel() }
    }

    suspend fun alternarFavorito(corrida: CorridaFavorita) = withContext(Dispatchers.IO) {
        val entity = CorridaFavoritaEntity.fromModel(corrida)
        dao.inserir(entity)
    }

    suspend fun atualizar(corrida: CorridaFavorita) = withContext(Dispatchers.IO) {
        val entity = CorridaFavoritaEntity.fromModel(corrida)
        dao.atualizar(entity)
    }

    suspend fun remover(id: String) = withContext(Dispatchers.IO) {
        dao.deletarPorId(id)
    }
}
