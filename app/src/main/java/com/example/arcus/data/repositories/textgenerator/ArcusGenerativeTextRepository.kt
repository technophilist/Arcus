package com.example.arcus.data.repositories.textgenerator

import com.example.arcus.data.getBodyOrThrowException
import com.example.arcus.data.local.textgeneration.GeneratedTextCacheDatabaseDao
import com.example.arcus.data.local.textgeneration.GeneratedTextForLocationEntity
import com.example.arcus.data.remote.languagemodel.TextGeneratorClient
import com.example.arcus.data.remote.languagemodel.models.MessageDTO
import com.example.arcus.data.remote.languagemodel.models.TextGenerationPromptBody
import com.example.arcus.domain.models.CurrentWeatherDetails
import kotlinx.coroutines.CancellationException
import javax.inject.Inject


class ArcusGenerativeTextRepository @Inject constructor(
    private val textGeneratorClient: TextGeneratorClient,
    private val generatedTextCacheDatabaseDao: GeneratedTextCacheDatabaseDao,
) : GenerativeTextRepository {

    override suspend fun generateTextForWeatherDetails(weatherDetails: CurrentWeatherDetails): Result<String> {
        val generatedTextEntity =
            generatedTextCacheDatabaseDao.getGeneratedTextForLocation(weatherDetails.nameOfLocation)
        if (generatedTextEntity != null) return Result.success(generatedTextEntity.generatedDescription)
        // prompts
        val systemPrompt = """
            You are a weather reporter. Generate a very short, but whimsical description of the weather,
            based on the given information.
        """.trimIndent()
        val userPrompt = """
            location = ${weatherDetails.nameOfLocation};
            currentTemperature = ${weatherDetails.temperatureRoundedToInt};
            weatherCondition = ${weatherDetails.weatherCondition};
            isNight = ${weatherDetails.isDay != 1}
        """.trimIndent()
        // prompt messages
        val promptMessages = listOf(
            MessageDTO(role = "system", content = systemPrompt),
            MessageDTO(role = "user", content = userPrompt)
        )
        val textGenerationPrompt = TextGenerationPromptBody(
            messages = promptMessages,
            model = "gpt-3.5-turbo-0613"
        )
        // request to generate text based on prompt body
        return try {
            // generate text
            val generatedTextResponse = textGeneratorClient.getModelResponseForConversations(
                textGenerationPostBody = textGenerationPrompt
            ).getBodyOrThrowException()
                .generatedResponses
                .first().message
                .content
            // save the generated text in database
            val generatedTextForLocationEntity = GeneratedTextForLocationEntity(
                nameOfLocation = weatherDetails.nameOfLocation,
                temperature = weatherDetails.temperatureRoundedToInt,
                conciseWeatherDescription = weatherDetails.weatherCondition,
                generatedDescription = generatedTextResponse
            )
            generatedTextCacheDatabaseDao.addGeneratedTextForLocation(generatedTextForLocationEntity)
            // return the result
            Result.success(generatedTextResponse)
        } catch (exception: Exception) {
            if (exception is CancellationException) throw exception
            Result.failure(exception)
        }
    }

}