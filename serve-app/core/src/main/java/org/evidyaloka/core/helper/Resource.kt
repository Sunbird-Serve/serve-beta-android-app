package org.evidyaloka.core.helper


/**
 * @author Madhankumar
 * created on 27-12-2020
 *
 */
sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class GenericError<out T>(val code: Int? = null, val error: ErrorData? = null, val data: T?) : Resource<T>()
}
