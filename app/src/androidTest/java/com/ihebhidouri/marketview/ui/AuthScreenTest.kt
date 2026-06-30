package com.ihebhidouri.marketview.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.ihebhidouri.marketview.ui.screens.AuthScreen
import com.ihebhidouri.marketview.ui.theme.MarketViewTheme
import com.ihebhidouri.marketview.viewmodels.AuthUiState
import org.junit.Rule
import org.junit.Test

class AuthScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun authScreen_initialState_showsLoginForm() {
        composeTestRule.setContent {
            MarketViewTheme {
                AuthScreen(
                    uiState = AuthUiState(),
                    onLogin = { _, _ -> },
                    onSignUp = { _, _, _ -> },
                    onClearError = {}
                )
            }
        }

        composeTestRule.onNodeWithText("MarketView").assertIsDisplayed()
        composeTestRule.onNodeWithText("Welcome Back").assertIsDisplayed()
        composeTestRule.onNodeWithText("Log In").assertIsDisplayed()
    }

    @Test
    fun authScreen_emptyFields_loginButtonDisabled() {
        composeTestRule.setContent {
            MarketViewTheme {
                AuthScreen(
                    uiState = AuthUiState(),
                    onLogin = { _, _ -> },
                    onSignUp = { _, _, _ -> },
                    onClearError = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Log In")
            .assertIsNotEnabled()
    }

    @Test
    fun authScreen_filledFields_loginButtonEnabled() {
        composeTestRule.setContent {
            MarketViewTheme {
                AuthScreen(
                    uiState = AuthUiState(),
                    onLogin = { _, _ -> },
                    onSignUp = { _, _, _ -> },
                    onClearError = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Email").performTextInput("test@email.com")
        composeTestRule.onNodeWithText("Password").performTextInput("password123")
        composeTestRule.onNodeWithText("Log In")
            .assertIsEnabled()
    }

    @Test
    fun authScreen_toggleToSignUp_showsUsernameField() {
        composeTestRule.setContent {
            MarketViewTheme {
                AuthScreen(
                    uiState = AuthUiState(),
                    onLogin = { _, _ -> },
                    onSignUp = { _, _, _ -> },
                    onClearError = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Don't have an account? Sign Up")
            .performClick()
        composeTestRule.onNodeWithText("Create Account").assertIsDisplayed()
        composeTestRule.onNodeWithText("Username").assertIsDisplayed()
    }

    @Test
    fun authScreen_withError_showsErrorMessage() {
        composeTestRule.setContent {
            MarketViewTheme {
                AuthScreen(
                    uiState = AuthUiState(error = "Invalid credentials"),
                    onLogin = { _, _ -> },
                    onSignUp = { _, _, _ -> },
                    onClearError = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Invalid credentials").assertIsDisplayed()
    }
}