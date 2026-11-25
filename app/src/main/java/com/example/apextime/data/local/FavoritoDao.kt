package com.example.apextime.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun inserir(corrida: CorridaFavoritaEntity): Long

    @Query("SELECT * FROM corridas_favoritas")
    fun listarTodos(): Flow<List<CorridaFavoritaEntity>>

    @Update
    fun atualizar(corrida: CorridaFavoritaEntity): Int

    @Delete
    fun deletar(corrida: CorridaFavoritaEntity): Int

    @Query("DELETE FROM corridas_favoritas WHERE id = :id")
    fun deletarPorId(id: String): Int
}
