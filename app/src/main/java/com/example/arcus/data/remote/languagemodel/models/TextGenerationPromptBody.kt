package com.example.arcus.data.remote.languagemodel.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * A class that contains the prompt messages and it's associated meta-data that a large language
 * model API uses to generate a response in the form of [GeneratedTextResponse].
 */
@JsonClass(generateAdapter = true)
data class TextGenerationPromptBody(
    val messages: List<MessageDTO>,
    val model: String,
    @Json(name = "max_tokens") val maxResponseTokens: Int = 150
)