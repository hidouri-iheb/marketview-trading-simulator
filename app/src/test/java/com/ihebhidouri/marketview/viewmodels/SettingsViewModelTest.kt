package com.ihebhidouri.marketview.viewmodels

import com.ihebhidouri.marketview.MainDispatcherRule
import com.ihebhidouri.marketview.data.datastore.ThemePreferencesRepository
import com.ihebhidouri.marketview.models.ThemeMode
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = mockk<ThemePreferencesRepository>(relaxed = true)

    @Test
    fun setThemeMode_callsRepository() = runTest {
        every { repository.themeMode } returns flowOf(ThemeMode.DARK)

        val viewModel = SettingsViewModel(repository)

        viewModel.setThemeMode(ThemeMode.LIGHT)

        coVerify {
            repository.setThemeMode(ThemeMode.LIGHT)
        }
    }
}