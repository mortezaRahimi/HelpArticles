package com.mortex.helparticles.data.remote

import com.mortex.helparticles.data.remote.JsonMockResponses.articleDetailJson
import com.mortex.helparticles.data.remote.JsonMockResponses.articleListJson
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * Hard-coded JSON responses to simulate a backend.
 *
 * Used by MockInterceptor class to serve JSON for:
 *  - GET /articles              -> [articleListJson]
 *  - GET /articles/{id}         -> [articleDetailJson]
 *
 * Special IDs (only in the list JSON):
 *  - "backend_error"       -> interceptor returns HTTP 500 with backend error JSON
 *  - "connectivity_error"  -> interceptor throws IOException (simulated no network)
 */
object JsonMockResponses {

    fun articleListJson(): String {
        val now = isoNow()
        // Note: \n in strings are literal "\n" so JSON stays valid, parser will convert to newlines.
        return """
            [
              {
                "id": "1",
                "title": "Getting Started with the Help Center",
                "summary": "It would be cached after visiting details.",
                "updatedAt": "$now"
              },
              {
                "id": "2",
                "title": "Sample Article",
                "summary": "It would be cached after visiting details.",
                "updatedAt": "$now"
              },
              {
                "id": "3",
                "title": "Offline & Error Handling",
                "summary": "It would be cached after visiting details.",
                "updatedAt": "$now"
              },
              {
                "id": "backend_error",
                "title": "Backend Error Example",
                "summary": "Opening this article will trigger a 500 backend error if online.",
                "updatedAt": "$now"
              },
              {
                "id": "connectivity_error",
                "title": "Connectivity Error Example",
                "summary": "Opening this article will simulate a connectivity failure both online and offline.",
                "updatedAt": "$now"
              }
           
            ]
        """.trimIndent()
    }

    fun articleDetailJson(id: String): String? {
        val now = isoNow()

        return when (id) {
            "1" -> """
                {
                  "id": "1",
                  "title": "Getting Started with the Help Center",
                  "content": "# Getting Started\n\nWelcome to the **Help Center** example.\n\n- Browse the list of help articles.\n- Tap an article to see the full content.\n- Use search to filter articles.",
                  "updatedAt": "$now"
                }
            """.trimIndent()

            "2" -> """
                {
                  "id": "2",
                  "title": "Sample Article Title",
                  "content": "# Sample Article\n\nThis article explains:\n\n1. How the cache stores the last successful result.\n2. How the app shows cached content when offline.\n3. How the Retry button reloads from the mock backend.",
                  "updatedAt": "$now"
                }
            """.trimIndent()

            "3" -> """
                {
                  "id": "3",
                  "title": "Offline & Error Handling",
                  "content": "- The app should show cached content if available.\n- Check how connectivity vs backend errors are displayed in the UI.",
                  "updatedAt": "$now"
                }
            """.trimIndent()

            "4" -> """
                {
                  "id": "4",
                  "title": "Backend Error Example",
                  "content": "Opening this article will trigger a 500 backend error if online.",
                  "updatedAt": "$now"
                }
            """.trimIndent()

            "5" -> """
                {
                  "id": "5",
                  "title": "Connectivity Error Example",
                  "content": "Opening this article will simulate a connectivity failure both online and offline.",
                  "updatedAt": "$now"
                }
            """.trimIndent()


            // For "backend_error" and "connectivity_error" we don't return detail JSON here:
            // the interceptor will either return a backend error body or throw IOException.
            else -> null
        }
    }

    private fun isoNow(): Instant {
        return Clock.System.now()
    }
}
