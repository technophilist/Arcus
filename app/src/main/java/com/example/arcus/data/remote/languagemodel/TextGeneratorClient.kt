package com.example.arcus.data.remote.languagemodel

import com.example.arcus.data.remote.languagemodel.models.GeneratedTextResponse
import com.example.arcus.data.remote.languagemodel.models.TextGenerationPromptBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * An interface that defines TextGenerator API's that generate text based on a LLM (Large Language Model).
 */
interface TextGeneratorClient {

    /**
     * Returns a response containing the generated text based on the provided prompt.
     * @param textGenerationPostBody The request body containing the prompt and LLM model information.
     * @return A response containing the generated text.
     */
    @POST(TextGeneratorClientConstants.Endpoints.CHAT_COMPLETION_END_POINT)
    suspend fun getModelResponseForConversations(
        @Body textGenerationPostBody: TextGenerationPromptBody
    ): Response<GeneratedTextResponse>

}
