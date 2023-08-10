package com.example.arcus.data.repositories.weather

import com.example.arcus.data.getBodyOrThrowException
import com.example.arcus.data.local.weather.ArcusDatabaseDao
import com.example.arcus.data.local.weather.SavedWeatherLocationEntity
import com.example.arcus.data.remote.languagemodel.TextGeneratorClient
import com.example.arcus.data.remote.languagemodel.models.MessageDTO
import com.example.arcus.data.remote.languagemodel.models.TextGenerationPromptBody
import com.example.arcus.data.remote.weather.WeatherClient
import com.example.arcus.domain.models.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import javax.inject.Inject

/**
 * The default concrete implementation of [WeatherRepository].
 */
class ArcusWeatherRepository @Inject constructor(
    private val weatherClient: WeatherClient,
    private val textGeneratorClient: TextGeneratorClient,
    private val arcusDatabaseDao: ArcusDatabaseDao
) : WeatherRepository {

    override suspend fun fetchWeatherForLocation(
        nameOfLocation: String,
        latitude: String,
        longitude: String
    ): Result<CurrentWeatherDetails> = try {
        val response = weatherClient.getWeatherForCoordinates(
            latitude = latitude,
            longitude = longitude
        )
        Result.success(response.getBodyOrThrowException().toCurrentWeatherDetails(nameOfLocation))
    } catch (exception: Exception) {
        if (exception is CancellationException) throw exception
        Result.failure(exception)
    }

    override fun getSavedLocationsListStream(): Flow<List<SavedLocation>> = arcusDatabaseDao
        .getAllWeatherEntitiesMarkedAsNotDeleted()
        .map { savedLocationEntitiesList -> savedLocationEntitiesList.map { it.toSavedLocation() } }

    override suspend fun saveWeatherLocation(
        nameOfLocation: String,
        latitude: String,
        longitude: String
    ) {
        val savedWeatherEntity = SavedWeatherLocationEntity(
            nameOfLocation = nameOfLocation,
            latitude = latitude,
            longitude = longitude
        )
        arcusDatabaseDao.addSavedWeatherEntity(savedWeatherEntity)
    }

    override suspend fun deleteWeatherLocationFromSavedItems(briefWeatherLocation: BriefWeatherDetails) {
        val savedLocationEntity = briefWeatherLocation.toSavedWeatherLocationEntity()
        arcusDatabaseDao.markWeatherEntityAsDeleted(savedLocationEntity.nameOfLocation)
    }

    override suspend fun permanentlyDeleteWeatherLocationFromSavedItems(briefWeatherLocation: BriefWeatherDetails) {
        briefWeatherLocation.toSavedWeatherLocationEntity().run {
            arcusDatabaseDao.deleteSavedWeatherEntity(this)
        }
    }

    override suspend fun fetchHourlyPrecipitationProbabilities(
        latitude: String,
        longitude: String,
        dateRange: ClosedRange<LocalDate>
    ): Result<List<PrecipitationProbability>> = try {
        val precipitationProbabilities = weatherClient.getHourlyForecast(
            latitude = latitude,
            longitude = longitude,
            startDate = dateRange.start,
            endDate = dateRange.endInclusive
        ).getBodyOrThrowException().toPrecipitationProbabilities()
        Result.success(precipitationProbabilities)
    } catch (exception: Exception) {
        if (exception is CancellationException) throw exception
        Result.failure(exception)
    }

    override suspend fun fetchHourlyForecasts(
        latitude: String,
        longitude: String,
        dateRange: ClosedRange<LocalDate>
    ): Result<List<HourlyForecast>> = try {
        val hourlyForecasts = weatherClient.getHourlyForecast(
            latitude = latitude,
            longitude = longitude,
            startDate = dateRange.start,
            endDate = dateRange.endInclusive
        ).getBodyOrThrowException().toHourlyForecasts()
        Result.success(hourlyForecasts)
    } catch (exception: Exception) {
        if (exception is CancellationException) throw exception
        Result.failure(exception)
    }

    override suspend fun fetchAdditionalWeatherInfoItemsListForCurrentDay(
        latitude: String,
        longitude: String,
    ): Result<List<SingleWeatherDetail>> = try {
        val additionalWeatherInfoItemsList = weatherClient.getAdditionalDailyForecastVariables(
            latitude = latitude,
            longitude = longitude,
            startDate = LocalDate.now(),
            endDate = LocalDate.now()
        ).getBodyOrThrowException().toSingleWeatherDetailList()
        Result.success(additionalWeatherInfoItemsList)
    } catch (exception: Exception) {
        if (exception is CancellationException) throw exception
        Result.failure(exception)
    }

    override suspend fun tryRestoringDeletedWeatherLocation(nameOfLocation: String) {
        arcusDatabaseDao.markWeatherEntityAsUnDeleted(nameOfLocation)
    }

    override suspend fun fetchGeneratedSummaryForWeatherDetails(currentWeatherDetails: CurrentWeatherDetails): Result<String> {
        // prompts
        val systemPrompt = """
            You are a weather reporter. Generate a very short, but whimsical description of the weather,
            based on the given information.
        """.trimIndent()
        val userPrompt = """
            location = ${currentWeatherDetails.nameOfLocation};
            currentTemperature = ${currentWeatherDetails.temperatureRoundedToInt};
            weatherCondition = ${currentWeatherDetails.weatherCondition};
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
            val generatedTextResponse = textGeneratorClient.getModelResponseForConversations(
                textGenerationPostBody = textGenerationPrompt
            ).getBodyOrThrowException()
            Result.success(generatedTextResponse.generatedResponses.first().message.content)
        } catch (exception: Exception) {
            if (exception is CancellationException) throw exception
            Result.failure(exception)
        }
    }
}