package com.example.ai

import com.google.firebase.FirebaseApp
import com.google.firebase.ai.FirebaseAI
import com.google.firebase.ai.type.GenerativeBackend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 * Gemini-backed shopping intelligence.
 * Uses Gemini 2.5 Pro first and falls back to Flash for availability/latency.
 * The Firebase AI SDK keeps credentials out of the APK source.
 */
object PricePilotAi {
    private val firebaseApp by lazy { FirebaseApp.getInstance() }

    private val proModel by lazy {
        FirebaseAI.getInstance(firebaseApp, GenerativeBackend.googleAI())
            .generativeModel("gemini-2.5-pro")
    }

    private val flashModel by lazy {
        FirebaseAI.getInstance(firebaseApp, GenerativeBackend.googleAI())
            .generativeModel("gemini-2.5-flash")
    }

    suspend fun ask(question: String, productContext: String = ""): String = withContext(Dispatchers.IO) {
        require(question.isNotBlank()) { "Please enter a question." }
        val prompt = """
            You are PricePilot AI, a helpful shopping assistant for Indian users.
            Answer naturally and concisely in the user's language.
            Never invent prices, availability, sellers or product specifications.
            Use supplied product context for factual claims; if a fact is missing, say so.
            You may still give general shopping advice when product context is missing.

            PRODUCT CONTEXT:
            ${productContext.ifBlank { "No live product context is currently available." }}

            USER QUESTION:
            ${question.trim()}
        """.trimIndent()

        var lastError: Throwable? = null
        repeat(2) { attempt ->
            try {
                val text = proModel.generateContent(prompt).text?.trim().orEmpty()
                if (text.isNotBlank()) return@withContext text
            } catch (e: Throwable) {
                lastError = e
                if (attempt == 0) delay(350)
            }
        }

        try {
            val text = flashModel.generateContent(prompt).text?.trim().orEmpty()
            if (text.isNotBlank()) return@withContext text
        } catch (e: Throwable) {
            lastError = e
        }

        val detail = lastError?.message?.takeIf { it.isNotBlank() }
        "I couldn't reach Gemini right now${if (detail != null) ": $detail" else ". Please try again in a moment."}"
    }

    suspend fun summarizeBestDeal(productContext: String): String = ask(
        "Compare the live offers and tell me which is the best overall deal. Consider price, discount, rating, availability and seller. Give the recommendation first, then one short trade-off.",
        productContext
    )
}
