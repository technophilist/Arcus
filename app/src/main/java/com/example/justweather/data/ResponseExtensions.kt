package com.example.justweather.data

import retrofit2.Response

/**
 * Returns the [Response.body] if the response is not null, otherwise it throws an [Exception].
 * The message of the exception thrown, includes the response code and error body (if present).
 */
fun <T> Response<T>.getBodyOrThrowException(): T {
    return body() ?: throw Exception("${code()}: ${message()}")
}
