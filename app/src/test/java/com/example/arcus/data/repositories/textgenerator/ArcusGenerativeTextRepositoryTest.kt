package com.example.arcus.data.repositories.textgenerator

import com.example.arcus.data.local.textgeneration.GeneratedTextCacheDatabaseDao
import com.example.arcus.data.local.textgeneration.GeneratedTextForLocationEntity
import com.example.arcus.data.remote.languagemodel.TextGeneratorClient
import com.example.arcus.data.remote.languagemodel.models.GeneratedTextResponse
import com.example.arcus.data.remote.languagemodel.models.MessageDTO
import com.example.arcus.domain.models.location.Coordinates
import com.example.arcus.domain.models.weather.CurrentWeatherDetails
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class ArcusGenerativeTextRepositoryTest {

    private lateinit var textGeneratorClientMock: TextGeneratorClient
    private lateinit var generativeTextRepository: ArcusGenerativeTextRepository

    @Before
    fun setup() {
        textGeneratorClientMock = mock {
            val fakeResponses = listOf(
                GeneratedTextResponse.GeneratedResponse(
                    message = MessageDTO(role = "", content = "")
                )
            )
            val fakeGeneratedTextResponse = GeneratedTextResponse(
                id = "",
                created = 1,
                generatedResponses = fakeResponses
            )
            onBlocking { getModelResponseForConversations(any()) }
                .doReturn(Response.success(fakeGeneratedTextResponse))
        }
        generativeTextRepository = ArcusGenerativeTextRepository(
            textGeneratorClient = textGeneratorClientMock,
            generatedTextCacheDatabaseDao = TestGeneratedTextCacheDatabaseDao()
        )
    }

    @Test
    fun `Generated text caching test`() = runTest {
        // given an instance of CurrentWeatherDetails
        val fakeWeatherDetails = CurrentWeatherDetails(
            nameOfLocation = "Seattle",
            temperatureRoundedToInt = 72,
            weatherCondition = "Sunny",
            isDay = 1,
            iconResId = 1,
            imageResId = 1,
            coordinates = Coordinates(latitude = "47.6062", longitude = "-122.3321")
        )
        // when initially generating text based on the above instance
        generativeTextRepository.generateTextForWeatherDetails(fakeWeatherDetails)
        // the repository must reach out to the client and make a network request
        verify(textGeneratorClientMock).getModelResponseForConversations(any())
        // when generating text based on the above instance, for the second time
        generativeTextRepository.generateTextForWeatherDetails(fakeWeatherDetails)
        // the repository must not reach out to the client and make a request.
        // instead, it should return the originally generated string from the cache.
        verifyNoMoreInteractions(textGeneratorClientMock)
    }
}

private class TestGeneratedTextCacheDatabaseDao : GeneratedTextCacheDatabaseDao {
    private val savedItems = mutableListOf<GeneratedTextForLocationEntity>()
    override suspend fun addGeneratedTextForLocation(generatedTextForLocationEntity: GeneratedTextForLocationEntity) {
        savedItems.add(generatedTextForLocationEntity)
    }

    override suspend fun getGeneratedTextForLocation(nameOfLocation: String): GeneratedTextForLocationEntity? {
        return savedItems.firstOrNull { it.nameOfLocation == nameOfLocation }
    }

    override suspend fun deleteAllSavedText() {
        savedItems.clear()
    }
}