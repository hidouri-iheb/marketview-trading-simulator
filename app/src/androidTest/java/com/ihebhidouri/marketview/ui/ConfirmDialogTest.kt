package com.ihebhidouri.marketview.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.ihebhidouri.marketview.ui.components.ConfirmDialog
import com.ihebhidouri.marketview.ui.theme.MarketViewTheme
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class ConfirmDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun confirmDialog_displaysAllContent() {
        composeTestRule.setContent {
            MarketViewTheme {
                ConfirmDialog(
                    title = "Delete Portfolio",
                    message = "This will permanently delete this portfolio.",
                    confirmText = "Delete",
                    dismissText = "Cancel",
                    isDestructive = true,
                    onConfirm = {},
                    onDismiss = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Delete Portfolio").assertIsDisplayed()
        composeTestRule.onNodeWithText("This will permanently delete this portfolio.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Delete").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").assertIsDisplayed()
    }

    @Test
    fun confirmDialog_confirmButtonTriggersCallback() {
        var confirmed = false

        composeTestRule.setContent {
            MarketViewTheme {
                ConfirmDialog(
                    title = "Close Trade",
                    message = "Are you sure?",
                    confirmText = "Close",
                    onConfirm = { confirmed = true },
                    onDismiss = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Close").performClick()
        Assert.assertTrue(confirmed)
    }

    @Test
    fun confirmDialog_cancelButtonTriggersCallback() {
        var dismissed = false

        composeTestRule.setContent {
            MarketViewTheme {
                ConfirmDialog(
                    title = "Remove Stock",
                    message = "Remove AAPL from watchlist?",
                    confirmText = "Remove",
                    onConfirm = {},
                    onDismiss = { dismissed = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Cancel").performClick()
        Assert.assertTrue(dismissed)
    }

    @Test
    fun confirmDialog_defaultButtonLabels() {
        composeTestRule.setContent {
            MarketViewTheme {
                ConfirmDialog(
                    title = "Test",
                    message = "Test message",
                    onConfirm = {},
                    onDismiss = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Confirm").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").assertIsDisplayed()
    }
}