package com.mortex.helparticles

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.mortex.helparticles.presentation.ui.articles.ArticlesScreen
import com.mortex.helparticles.presentation.ui.articles.ArticlesUiState
import com.mortex.helparticles.presentation.ui.theme.HelpArticlesTheme
import com.mortex.helparticles.util.AppError
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ArticlesScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun error_state_shows_message_and_retry_triggers_callback() {
        // Flag to verify callback invocation
        var retryCalled = false

        val uiState = ArticlesUiState(
            isLoading = false,
            articles = emptyList(),
            filteredArticles = emptyList(),
            searchQuery = "",
            error = AppError.Connectivity, // simulate network error
            isOnline = false
        )

        composeRule.setContent {
            HelpArticlesTheme {
                ArticlesScreen(
                    uiState = uiState,
                    onArticleClick = { /* no-op */ },
                    onRetry = { retryCalled = true },
                    onSearch = { /* no-op */ }
                )
            }
        }

        // Check error text is displayed
        composeRule.onNodeWithText("No Internet connection.")
            .assertIsDisplayed()

        // Click Retry button
        composeRule.onNodeWithText("Retry")
            .assertIsDisplayed()
            .performClick()

        // Verify callback has been invoked
        assertTrue("Retry callback should be called when Retry button is clicked", retryCalled)
    }
}
