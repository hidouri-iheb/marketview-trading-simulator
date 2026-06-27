package com.ihebhidouri.marketview
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.ihebhidouri.marketview.ui.theme.MarketViewTheme
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ihebhidouri.marketview.models.ThemeMode
import com.ihebhidouri.marketview.viewmodels.SettingsViewModel
import androidx.compose.ui.platform.LocalContext
import com.ihebhidouri.marketview.ui.screens.AuthScreen
import com.ihebhidouri.marketview.viewmodels.MarketViewViewModelFactory
import com.ihebhidouri.marketview.viewmodels.AuthViewModel
import com.ihebhidouri.marketview.viewmodels.SettingsUiState
import com.ihebhidouri.marketview.viewmodels.AuthUiState
import com.ihebhidouri.marketview.ui.navigation.MainContent
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val app = LocalContext.current.applicationContext as MarketViewApplication

            val viewModelFactory = MarketViewViewModelFactory(
                stockRepository = app.stockRepository,
                watchlistRepository = app.watchlistRepository,
                themeRepository = app.themePreferencesRepository,
                portfolioRepository = app.portfolioRepository,
                authRepository = app.authRepository
            )

            val settingsViewModel: SettingsViewModel = viewModel(factory = viewModelFactory)
            val authViewModel: AuthViewModel = viewModel(factory = viewModelFactory)
            val settingsState by settingsViewModel.uiState.collectAsStateWithLifecycle()
            val authState by authViewModel.uiState.collectAsStateWithLifecycle()
            val isDarkTheme = settingsState.themeMode == ThemeMode.DARK

            MarketViewTheme(darkTheme = isDarkTheme) {
                MarketViewApp(
                    viewModelFactory = viewModelFactory,
                    settingsState = settingsState,
                    settingsViewModel = settingsViewModel,
                    authViewModel = authViewModel,
                    authState = authState
                )
            }
        }
    }
}

@Composable
fun MarketViewApp(
    viewModelFactory: MarketViewViewModelFactory,
    settingsState: SettingsUiState,
    settingsViewModel: SettingsViewModel,
    authViewModel: AuthViewModel,
    authState: AuthUiState
) {
    if (authState.isLoggedIn) {
        MainContent(
            viewModelFactory = viewModelFactory,
            settingsState = settingsState,
            settingsViewModel = settingsViewModel,
            authViewModel = authViewModel,
            authState = authState
        )
    } else {
        AuthScreen(
            uiState = authState,
            onLogin = authViewModel::login,
            onSignUp = { username, email, password ->
                authViewModel.signUp(username, email, password)
            },
            onClearError = authViewModel::clearError
        )
    }
}



