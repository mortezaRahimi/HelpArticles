package com.mortex.helparticles.data.remote

import com.mortex.helparticles.core.AppDispatchers
import com.mortex.helparticles.core.DefaultAppDispatchers
import com.mortex.helparticles.data.model.ArticleDetailDto
import com.mortex.helparticles.data.model.ArticleSummaryDto
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

/**
 * Remote data source that talks to the mock HTTP api
 * via OkHttp + MockHelpArticlesInterceptor.
 */
class FakeRemoteDataSrc(
    private val client: OkHttpClient,
    private val json: Json,
    baseUrl: String = "https://mortex.help.local/",
    private val artificialDelayMillis: Long = 250L,
    private val appDispatchers: AppDispatchers = DefaultAppDispatchers,
) {

    private val baseHttpUrl = baseUrl.toHttpUrl()

    suspend fun fetchArticleSummaries(): List<ArticleSummaryDto> =

        get(
            pathSegments = listOf("articles"),
            delayMillis = artificialDelayMillis
        )

    suspend fun fetchArticleDetail(id: String): ArticleDetailDto =
        get(
            pathSegments = listOf("articles", id),
            delayMillis = artificialDelayMillis
        )

    /* Generic GET helper that:
    * - builds URL from [baseHttpUrl] and [pathSegments]
    * - executes a blocking OkHttp call on [ioDispatcher]
    * - parses success body into [T] using kotlinx.serialization
    * - parses error body into [BackendException] and throws it
    */
    private suspend inline fun<reified T>  get(
        pathSegments: List<String>,
        delayMillis: Long = 0L,
    ): T = withContext(appDispatchers.io) {
        if (delayMillis > 0) {
            delay(delayMillis)
        }

        val urlBuilder = baseHttpUrl.newBuilder()
        pathSegments.forEach { segment ->
            urlBuilder.addPathSegment(segment)
        }
        val url = urlBuilder.build()

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        val response = client.newCall(request).execute()

        response.use { resp ->
            val bodyString = resp.body?.string()
                ?: throw IOException("Empty response body for ${url.encodedPath}")

            if (!resp.isSuccessful) {
                throwBackendException(bodyString, resp.code)
            }

            try {
                json.decodeFromString<T>(bodyString)
            } catch (e: Exception) {
                // Malformed payload → treat as backend error
                throw BackendException(
                    errorCode = resp.code,
                    errorTitle = "Malformed response: ${e.message}",
                    message = "Failed to parse server response."
                )
            }
        }
    }

    private fun throwBackendException(body: String, httpCode: Int): Nothing {
        val errorDto = runCatching {
            json.decodeFromString<BackendErrorDto>(body)
        }.getOrElse {
            throw BackendException(
                errorCode = httpCode,
                errorTitle = null,
                message = body.take(200)
            )
        }
        throw BackendException(
            errorCode = errorDto.errorCode ?: httpCode,
            errorTitle = errorDto.errorTitle,
            message = errorDto.errorMessage
        )
    }
}
