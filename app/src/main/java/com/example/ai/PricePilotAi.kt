package com.example.ai

import com.google.firebase.ai.FirebaseAI
import com.google.firebase.ai.type.GenerativeBackend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** In-app AI layer for product understanding, deal summaries and shopping advice. */
object PricePilotAi {
    private val model by lazy {
        FirebaseAI.getInstance(GenerativeBackend.googleAI()).generativeModel("gemini-2.5-flash")
    }

    suspend fun ask(question: String, productContext: String = ""): String = withContext(Dispatchers.IO) {
        val prompt = """
            You are PricePilot AI, a concise shopping assistant for Indian users.
            Never invent prices, availability, sellers or product specifications.
            Use only the supplied product context for factual claims.
            If information is missing, say that it is unavailable.
            Give practical, easy-to-understand advice in the user's language.

            PRODUCT CONTEXT:
            $productContext

            USER QUESTION:
            $question
        """.trimIndent()
        model.generateContent(prompt).text?.trim().orEmpty().ifBlank {
            "I couldn't generate an AI answer right now. Please try again."
        }
    }

    suspend fun summarizeBestDeal(productContext: String): String = ask(
        "Compare the available offers and explain which is the best value. Mention the lowest available price, store and important caveats only when present in the context.",
        productContext
    )
}
