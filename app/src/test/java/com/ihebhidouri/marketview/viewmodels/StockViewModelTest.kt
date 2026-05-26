package com.ihebhidouri.marketview.viewmodels

import com.ihebhidouri.marketview.MainDispatcherRule
import com.ihebhidouri.marketview.repository.StockRepository
import io.mockk.mockk
import kotlinx.coroutines.flow.emptyFlow
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class StockViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = mockk<StockRepository>(relaxed = true) {
        io.mockk.every { getStocks() } returns emptyFlow()
    }

    @Test
    fun onSearchQueryChange_updatesSearchQuery() {
        val viewModel = StockViewModel(repository)

        viewModel.onSearchQueryChange("AAPL")

        assertEquals("AAPL", viewModel.searchQuery.value)
    }
}