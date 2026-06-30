package com.ihebhidouri.marketview.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.ihebhidouri.marketview.models.ThemeMode
import com.ihebhidouri.marketview.ui.screens.SettingsScreen
import com.ihebhidouri.marketview.ui.theme.MarketViewTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun settingsScreen_displaysProfileInfo() {
        composeTestRule.setContent {
            MarketViewTheme {
                SettingsScreen(
                    themeMode = ThemeMode.DARK,
                    onThemeModeChange = {},
                    onTradeHistoryClick = {},
                    onLogout = {},
                    displayName = "TestUser",
                    email = "test@email.com"
                )
            }
        }

        composeTestRule.onNodeWithText("Settings").assertIsDisplayed()
        composeTestRule.onNodeWithText("TestUser").assertIsDisplayed()
        composeTestRule.onNodeWithText("test@email.com").assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysThemeOptions() {
        composeTestRule.setContent {
            MarketViewTheme {
                SettingsScreen(
                    themeMode = ThemeMode.DARK,
                    onThemeModeChange = {},
                    onTradeHistoryClick = {},
                    onLogout = {},
                    displayName = "TestUser",
                    email = "test@email.com"
                )
            }
        }

        composeTestRule.onNodeWithText("Light").assertIsDisplayed()
        composeTestRule.onNodeWithText("Dark").assertIsDisplayed()
    }

    @Test
    fun settingsScreen_themeChangeCallsCallback() {
        var selectedMode: ThemeMode? = null

        composeTestRule.setContent {
            MarketViewTheme {
                SettingsScreen(
                    themeMode = ThemeMode.DARK,
                    onThemeModeChange = { selectedMode = it },
                    onTradeHistoryClick = {},
                    onLogout = {},
                    displayName = "TestUser",
                    email = "test@email.com"
                )
            }
        }

        composeTestRule.onNodeWithText("Light").performClick()
        assertEquals(ThemeMode.LIGHT, selectedMode)
    }

    @Test
    fun settingsScreen_logoutShowsConfirmDialog() {
        composeTestRule.setContent {
            MarketViewTheme {
                SettingsScreen(
                    themeMode = ThemeMode.DARK,
                    onThemeModeChange = {},
                    onTradeHistoryClick = {},
                    onLogout = {},
                    displayName = "TestUser",
                    email = "test@email.com"
                )
            }
        }

        composeTestRule.onNodeWithText("Log Out").performClick()
        composeTestRule.onNodeWithText("Are you sure you want to log out?").assertIsDisplayed()
    }
}