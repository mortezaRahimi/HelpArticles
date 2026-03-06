package com.mortex.helparticles.data.remote

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

/**
 * OkHttp interceptor that acts as a self-contained mock HTTP backend.
 *
 * Endpoints:
 *  - GET /articles          -> list of ArticleSummaryDto
 *  - GET /articles/{id}     -> ArticleDetailDto
 *
 * Special behavior:
 *  - id == "backend_error"      -> HTTP 500 with backend error JSON
 *  - id == "connectivity_error" -> throws IOException (simulated network issue)
 */
class MockInterceptor(
    private val json: Json,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url
        val path = url.encodedPath

        // Simulate connectivity error via special ID
        if (path.endsWith("4")) {
            throw ConnectivityException("Simulated connectivity error from mock interceptor")
        }

        return when {
            path == "/articles" -> {
                val listJson = JsonMockResponses.articleListJson()
                successResponse(chain, listJson)
            }

            path.startsWith("/articles/") -> {
                val id = path.substringAfterLast("/")

                if (id == "5") {
                    backendErrorResponse(
                        chain = chain,
                        code = 500,
                        title = "Article unavailable",
                        message = "This article cannot be loaded at the moment."
                    )
                } else {
                    val detailMapJson = JsonMockResponses.articleDetailJson(id)
                    if (detailMapJson != null) {
                        successResponse(chain, detailMapJson)
                    } else {
                        backendErrorResponse(
                            chain = chain,
                            code = 404,
                            title = "Article not found",
                            message = "The requested article does not exist."
                        )
                    }
                }
            }

            else -> {
                backendErrorResponse(
                    chain = chain,
                    code = 404,
                    title = "Not found",
                    message = "Unknown endpoint: $path"
                )
            }
        }
    }

    private fun successResponse(
        chain: Interceptor.Chain,
        body: String,
    ): Response {
        val mediaType = "application/json".toMediaType()
        val bodyResp = body.toResponseBody(mediaType)

        return Response.Builder()
            .request(chain.request())
            .code(200)
            .message("OK")
            .protocol(Protocol.HTTP_1_1)
            .body(bodyResp)
            .build()
    }

    private fun backendErrorResponse(
        chain: Interceptor.Chain,
        code: Int,
        title: String?,
        message: String?,
    ): Response {
        val errorDto = BackendException(
            errorCode = code,
            errorTitle = title,
            message = message
        )
        val body = json.encodeToString(errorDto)

        val mediaType = "application/json".toMediaType()
        val bodyResp = body.toResponseBody(mediaType)

        return Response.Builder()
            .request(chain.request())
            .code(code)
            .message(title ?: "Error")
            .protocol(Protocol.HTTP_1_1)
            .body(bodyResp)
            .build()
    }
}
