package com.example.apextime

import com.example.apextime.data.CorridaFavorita
import com.example.apextime.data.FavoritoRepository
import com.example.apextime.data.local.CorridaFavoritaEntity
import com.example.apextime.data.local.FavoritoDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FakeFavoritoDao : FavoritoDao {
    private val dados = MutableStateFlow<List<CorridaFavoritaEntity>>(emptyList())

    override fun inserir(corrida: CorridaFavoritaEntity): Long {
        dados.update { lista ->
            val semAtual = lista.filterNot { it.id == corrida.id }
            semAtual + corrida
        }
        return 1L
    }

    override fun listarTodos(): Flow<List<CorridaFavoritaEntity>> = dados

    override fun atualizar(corrida: CorridaFavoritaEntity): Int {
        dados.update { lista ->
            lista.map { if (it.id == corrida.id) corrida else it }
        }
        return 1
    }

    override fun deletar(corrida: CorridaFavoritaEntity): Int {
        dados.update { lista -> lista.filterNot { it.id == corrida.id } }
        return 1
    }

    override fun deletarPorId(id: String): Int {
        dados.update { lista -> lista.filterNot { it.id == id } }
        return 1
    }
}

class FavoritoRepositoryTest {

    private lateinit var fakeDao: FakeFavoritoDao
    private lateinit var repository: FavoritoRepository

    private val corridaExemplo = CorridaFavorita(
        id = "f1_las_vegas",
        categoria = "Fórmula 1",
        nome = "Las Vegas Grand Prix",
        circuito = "Las Vegas Strip Street Circuit",
        dataHoraFormatada = "Domingo, 24/11 às 01:00",
        anotacaoPessoal = "Assistir de madrugada"
    )

    @Before
    fun setup() {
        fakeDao = FakeFavoritoDao()
        repository = FavoritoRepository(fakeDao)
    }

    @Test
    fun alternarFavorito_deveInserirCorridaQuandoNaoExiste() = runTest {
        repository.alternarFavorito(corridaExemplo)

        val lista = repository.favoritos.first()
        assertEquals(1, lista.size)
        assertEquals("Las Vegas Grand Prix", lista.first().nome)
        assertEquals("Assistir de madrugada", lista.first().anotacaoPessoal)
    }

    @Test
    fun alternarFavorito_deveRemoverCorridaQuandoJaExiste() = runTest {
        repository.alternarFavorito(corridaExemplo)
        assertTrue(repository.favoritos.first().any { it.id == "f1_las_vegas" })

        repository.remover("f1_las_vegas")
        assertFalse(repository.favoritos.first().any { it.id == "f1_las_vegas" })
    }

    @Test
    fun atualizar_deveSalvarNovaAnotacaoNaCorridaExistente() = runTest {
        repository.alternarFavorito(corridaExemplo)

        val corridaAtualizada = corridaExemplo.copy(anotacaoPessoal = "Anotacao Atualizada")
        repository.atualizar(corridaAtualizada)

        val lista = repository.favoritos.first()
        assertEquals("Anotacao Atualizada", lista.first().anotacaoPessoal)
    }

    @Test
    fun remover_deveExcluirCorridaDaLista() = runTest {
        repository.alternarFavorito(corridaExemplo)
        assertEquals(1, repository.favoritos.first().size)

        repository.remover("f1_las_vegas")
        assertTrue(repository.favoritos.first().isEmpty())
    }

    @Test
    fun toModel_deveConverterEntityParaModelCorretamente() {
        val entity = CorridaFavoritaEntity(
            id = "wec_spa",
            categoria = "FIA WEC",
            nome = "6 Hours of Spa",
            circuito = "Circuit de Spa-Francorchamps",
            dataHoraFormatada = "15/05/2027",
            anotacaoPessoal = "Anotação de Teste"
        )

        val model = entity.toModel()

        assertEquals("wec_spa", model.id)
        assertEquals("FIA WEC", model.categoria)
        assertEquals("6 Hours of Spa", model.nome)
        assertEquals("Anotação de Teste", model.anotacaoPessoal)
    }

    @Test
    fun fromModel_deveConverterModelParaEntityCorretamente() {
        val entity = CorridaFavoritaEntity.fromModel(corridaExemplo)

        assertEquals("f1_las_vegas", entity.id)
        assertEquals("Fórmula 1", entity.categoria)
        assertEquals("Las Vegas Grand Prix", entity.nome)
        assertEquals("Assistir de madrugada", entity.anotacaoPessoal)
    }
}
