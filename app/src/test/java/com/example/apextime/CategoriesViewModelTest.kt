package com.example.apextime

import com.example.apextime.data.CategoriesViewModel
import com.example.apextime.data.CategoryFilterTab
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CategoriesViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialUiState() {
        val viewModel = CategoriesViewModel()
        val state = viewModel.uiState.value

        assertNotNull(state)
        assertEquals(CategoryFilterTab.TEMPORADAS_ATIVAS, state.activeFilter)
    }

    @Test
    fun testFilterSelection() {
        val viewModel = CategoriesViewModel()

        viewModel.onFilterSelected(CategoryFilterTab.A_SEGUIR)
        assertEquals(CategoryFilterTab.A_SEGUIR, viewModel.uiState.value.activeFilter)

        viewModel.onFilterSelected(CategoryFilterTab.TEMPORADAS_ATIVAS)
        assertEquals(CategoryFilterTab.TEMPORADAS_ATIVAS, viewModel.uiState.value.activeFilter)
    }
}
