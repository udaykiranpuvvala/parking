package com.elite.parking

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    // Success state
    class Success<T>(data: T) : Resource<T>(data)

    // Failure state
    class Failure<T>(message: String) : Resource<T>(message = message)

    // Loading state
    class Loading<T> : Resource<T>()
}