package com.mortex.helparticles

import com.mortex.helparticles.domain.model.ArticleDetail
import com.mortex.helparticles.domain.model.ArticleSummary
import com.mortex.helparticles.domain.repository.ArticlesRepository
import com.mortex.helparticles.domain.usecase.GetArticleDetailUseCase
import com.mortex.helparticles.domain.usecase.GetArticlesUseCase
import com.mortex.helparticles.domain.usecase.RefreshArticlesIfStaleUseCase
import com.mortex.helparticles.util.AppError
import com.mortex.helparticles.util.AppResult
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for GetArticlesUseCase, GetArticleDetailUseCase, RefreshArticlesIfStaleUseCase.
 *
 * Focus:
 * - Correct delegation to ArticlesRepository
 * - Correct propagation of AppResult (Success/Error)
 * - Correct parameter passing (articleId)
 */
class ArticlesUseCasesTest {

    private val fakeRepository = FakeArticlesRepository()

    private val getArticlesUseCase = GetArticlesUseCase(fakeRepository)
    private val getArticleDetailUseCase = GetArticleDetailUseCase(fakeRepository)
    private val refreshArticlesIfStaleUseCase = RefreshArticlesIfStaleUseCase(fakeRepository)
    private val now = Clock.System.now()

    @Test
    fun `GetArticlesUseCase returns success result from repository`() = runTest {
        // Arrange
        val expectedArticles = listOf(
            ArticleSummary(
                id = "1",
                title = "Title 1",
                summary = "Summary 1",
                updatedAt = now
            ),
            ArticleSummary(
                id = "2",
                title = "Title 2",
                summary = "Summary 2",
                updatedAt = now
            )
        )

        fakeRepository.getArticlesResult = AppResult.Success(
            data = expectedArticles,
            isOnline = true,
            fromCache = false
        )

        // Act
        val result = getArticlesUseCase()

        // Assert
        assertTrue(result is AppResult.Success)
        result as AppResult.Success

        assertEquals(expectedArticles, result.data)
        assertTrue(result.isOnline)
        assertFalse(result.fromCache)
        assertEquals(1, fakeRepository.getArticlesCallCount)
    }

    @Test
    fun `GetArticleDetailUseCase passes articleId and propagates error`() = runTest {
        // Arrange
        val expectedError = AppError.Backend(
            code = 500,
            title = "Server error",
            message = "Something went wrong"
        )

        fakeRepository.getArticleDetailResult = AppResult.Error(expectedError)

        // Act
        val result = getArticleDetailUseCase("42")

        // Assert
        assertEquals("42", fakeRepository.lastRequestedDetailId)
        assertTrue(result is AppResult.Error)
        result as AppResult.Error
        assertEquals(expectedError, result.appError)
    }

    @Test
    fun `RefreshArticlesIfStaleUseCase delegates to repository and returns success`() = runTest {
        // Arrange
        val expectedArticles = listOf(

            ArticleSummary(
                id = "stale",
                title = "Stale article",
                summary = "Refreshed or cached",
                updatedAt = now
            )
        )

        fakeRepository.refreshArticlesIfStaleResult = AppResult.Success(
            data = expectedArticles,
            isOnline = true,
            fromCache = false
        )

        // Act
        val result = refreshArticlesIfStaleUseCase()

        // Assert
        assertEquals(1, fakeRepository.refreshArticlesIfStaleCallCount)
        assertTrue(result is AppResult.Success)
        result as AppResult.Success
        assertEquals(expectedArticles, result.data)
    }
}

/**
 * Simple fake implementation of ArticlesRepository for testing use cases.
 */
private class FakeArticlesRepository : ArticlesRepository {

    var getArticlesResult: AppResult<List<ArticleSummary>>? = null
    var getArticleDetailResult: AppResult<ArticleDetail>? = null
    var refreshArticlesIfStaleResult: AppResult<List<ArticleSummary>>? = null

    var getArticlesCallCount: Int = 0
        private set

    var refreshArticlesIfStaleCallCount: Int = 0
        private set

    var lastRequestedDetailId: String? = null
        private set

    override suspend fun getArticles(): AppResult<List<ArticleSummary>> {
        getArticlesCallCount++
        return getArticlesResult
            ?: error("getArticlesResult not set in FakeArticlesRepository")
    }

    override suspend fun getArticleDetail(articleId: String): AppResult<ArticleDetail> {
        lastRequestedDetailId = articleId
        return getArticleDetailResult
            ?: error("getArticleDetailResult not set in FakeArticlesRepository")
    }

    override suspend fun refreshArticlesIfStale(): AppResult<List<ArticleSummary>> {
        refreshArticlesIfStaleCallCount++
        return refreshArticlesIfStaleResult
            ?: error("refreshArticlesIfStaleResult not set in FakeArticlesRepository")
    }
}
