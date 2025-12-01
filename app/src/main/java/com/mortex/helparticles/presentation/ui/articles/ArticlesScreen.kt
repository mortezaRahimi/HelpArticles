package com.mortex.helparticles.presentation.ui.articles

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mortex.helparticles.R
import com.mortex.helparticles.domain.model.ArticleSummary
import com.mortex.helparticles.presentation.ui.components.EmptyView
import com.mortex.helparticles.presentation.ui.components.ErrorView
import com.mortex.helparticles.presentation.ui.components.LoadingView
import com.mortex.helparticles.presentation.ui.util.toDate
import com.mortex.helparticles.presentation.ui.util.toMessageResArticles

@Composable
fun ArticlesScreen(
    uiState: ArticlesUiState,
    onArticleClick: (String) -> Unit,
    onRetry: () -> Unit,
    onSearch: (String) -> Unit,
) {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        when {
            uiState.isLoading -> LoadingView()

            uiState.error != null -> ErrorView(
                message = stringResource(uiState.error.toMessageResArticles()),
                onRetry = onRetry
            )

            else -> {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = onSearch,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    placeholder = {
                        Text(
                            text = stringResource(R.string.search_articles),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    singleLine = true
                )
                if (uiState.filteredArticles.isEmpty())
                    EmptyView(message = stringResource(R.string.no_articles_found))
                else
                    ArticleList(
                        articles = uiState.filteredArticles,
                        onItemClick = onArticleClick
                    )
            }
        }
    }
}

@Composable
private fun ArticleList(
    articles: List<ArticleSummary>,
    onItemClick: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(articles) { item ->
            ArticleRow(item = item, onClick = { onItemClick(item.id) })
        }
    }
}

@Composable
private fun ArticleRow(
    item: ArticleSummary,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = item.title,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = item.summary,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = item.updatedAt.toDate(),
            style = MaterialTheme.typography.labelSmall
        )
        HorizontalDivider()
    }
}
