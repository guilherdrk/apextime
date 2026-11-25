package com.example.apextime.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.apextime.data.CorridaFavorita

@Entity(tableName = "corridas_favoritas")
data class CorridaFavoritaEntity(
    @PrimaryKey val id: String,
    val categoria: String,
    val nome: String,
    val circuito: String,
    val dataHoraFormatada: String,
    val anotacaoPessoal: String = "",
    val lembreteAtivo: Boolean = false
) {
    fun toModel(): CorridaFavorita = CorridaFavorita(
        id = id,
        categoria = categoria,
        nome = nome,
        circuito = circuito,
        dataHoraFormatada = dataHoraFormatada,
        anotacaoPessoal = anotacaoPessoal,
        lembreteAtivo = lembreteAtivo
    )

    companion object {
        fun fromModel(model: CorridaFavorita): CorridaFavoritaEntity = CorridaFavoritaEntity(
            id = model.id,
            categoria = model.categoria,
            nome = model.nome,
            circuito = model.circuito,
            dataHoraFormatada = model.dataHoraFormatada,
            anotacaoPessoal = model.anotacaoPessoal,
            lembreteAtivo = model.lembreteAtivo
        )
    }
}
