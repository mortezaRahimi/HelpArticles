package com.mortex.helparticles.presentation.ui.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mikepenz.markdown.m3.Markdown
import com.mortex.helparticles.R
import com.mortex.helparticles.presentation.ui.components.EmptyView
import com.mortex.helparticles.presentation.ui.components.ErrorView
import com.mortex.helparticles.presentation.ui.components.LoadingView
import com.mortex.helparticles.presentation.ui.components.OfflineBanner
import com.mortex.helparticles.presentation.ui.util.toMessageResDetails

@Composable
fun ArticleDetailScreen(
    uiState: ArticleDetailUiState,
    onRetry: () -> Unit,
) {
    when {
        uiState.isLoading -> LoadingView(stringResource(R.string.loading_article))

        uiState.error != null -> ErrorView(
            message = stringResource(uiState.error.toMessageResDetails()),
            onRetry = onRetry
        )

        uiState.article != null -> ArticleDetailContent(uiState)

        else -> EmptyView(stringResource(R.string.no_article_to_show))
    }
}

@Composable
private fun ArticleDetailContent(state: ArticleDetailUiState) {
    val article = state.article ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (!state.isOnline)
            OfflineBanner()

        Text(
            text = article.title,
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(Modifier.height(16.dp))

        Markdown(
            content = article.content,
        )
    }
}

