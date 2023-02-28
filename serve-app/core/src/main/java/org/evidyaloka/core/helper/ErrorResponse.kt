package org.evidyaloka.core.helper

import com.google.gson.annotations.SerializedName

/**
 * @author Madhankumar
 * created on 24-02-2021
 *
 */
class ErrorResponse(
    @SerializedName("error")
    val error: ErrorData = ErrorData(),
    @SerializedName("status")
    val status: String = "",
    @SerializedName("statusCode")
    val statusCode: Int = 0
)