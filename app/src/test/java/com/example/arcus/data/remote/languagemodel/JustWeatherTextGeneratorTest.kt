package com.example.arcus.data.remote.languagemodel

import com.example.arcus.data.remote.languagemodel.models.MessageDTO
import com.example.arcus.data.remote.languagemodel.models.TextGenerationPromptBody
import com.example.arcus.di.NetworkModule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


@OptIn(ExperimentalCoroutinesApi::class)
class JustWeatherTextGeneratorTest {

    private val textGeneratorClient = NetworkModule.provideTextGeneratorClient()

    @Test
    fun `Given a valid system & user prompt, the API must return a response with the generated text`() =
        runTest {
            val messages = listOf(
                MessageDTO(
                    role = "system",
                    content = "Generate a short and funny summary of the weather, based on the given details."
                ),
                MessageDTO(
                    role = "user",
                    content = "location:New York; max temp = 32 degrees; chance of rain = 30%;"
                )
            )
            val generatedTextResponse = textGeneratorClient.getModelResponseForConversations(
                textGenerationPostBody = TextGenerationPromptBody(
                    model = "gpt-3.5-turbo-0613",
                    messages = messages
                )
            ).also { println(it.body()) }
            assert(generatedTextResponse.isSuccessful)
        }
}