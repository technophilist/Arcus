package com.example.arcus.data.remote.languagemodel

import com.example.arcus.BuildConfig
import com.example.arcus.data.remote.languagemodel.models.GeneratedTextResponse
import com.example.arcus.data.remote.languagemodel.models.MessageDTO
import com.example.arcus.data.remote.languagemodel.models.TextGenerationPromptBody
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.CancellationException
import okhttp3.internal.EMPTY_RESPONSE
import retrofit2.Response
import java.util.UUID
import javax.inject.Inject

/**
 * An instance of [TextGeneratorClient] that uses Google's Gemini model under the hood.
 */
class GeminiTextGeneratorClient @Inject constructor() : TextGeneratorClient {

    private val generativeModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = BuildConfig.GOOGLE_GEMINI_API_KEY
    )

    override suspend fun getModelResponseForConversations(textGenerationPostBody: TextGenerationPromptBody): Response<GeneratedTextResponse> {
        return try {
            // Creating a list of type <Content>  always resulted in the
            // following exception to be thrown - ServerException with the message
            // "please ensure that multiturn requests ends with a user role or a function response."
            // Creating a list of type <Content>  always resulted in the
            // following exception to be thrown - ServerException with the message
            // "please ensure that multiturn requests ends with a user role or a function response."
            val prompt = textGenerationPostBody.messages.fold("") { acc, messageDTO ->
                acc + " ${messageDTO.content}"
            }
            val defaultErrorMessage =
                "Sorry, I'm having trouble responding to you. Please try again."
            val generatedResponse = GeneratedTextResponse.GeneratedResponse(
                message = MessageDTO(
                    role = "",
                    content = generativeModel.generateContent(prompt).text ?: defaultErrorMessage
                )
            )
            val currentTimeInSeconds = (System.currentTimeMillis() / 1000).toInt()
            val generatedTextResponse = GeneratedTextResponse(
                id = UUID.randomUUID().toString(),
                created = currentTimeInSeconds,
                generatedResponses = listOf(generatedResponse)
            )
            Response.success(generatedTextResponse)
        } catch (exception: Exception) {
            println(exception)
            if (exception is CancellationException) throw exception
            Response.error(400, EMPTY_RESPONSE)
        }
    }
}