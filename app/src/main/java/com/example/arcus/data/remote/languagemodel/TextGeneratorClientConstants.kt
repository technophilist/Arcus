package com.example.arcus.data.remote.languagemodel


/**
 * This object contains constants used by the [TextGeneratorClient].
 */
object TextGeneratorClientConstants {
    /**
     * The base URL of the [TextGeneratorClient]'s API.
     */
    const val BASE_URL = "https://api.openai.com/v1/chat/"

    object Endpoints {
        /**
         * The endpoint that is used to get the generated text.
         */
        const val CHAT_COMPLETION_END_POINT = "completions"
    }
}