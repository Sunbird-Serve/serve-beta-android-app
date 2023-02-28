package org.evidyaloka.core.partner.network.entity


import com.google.gson.annotations.SerializedName

class ErrorResponse(
    @SerializedName("error")
    val error: ErrorData = ErrorData(),
    @SerializedName("status")
    val status: String? = "",
    @SerializedName("statusCode")
    val statusCode: Int? = 0
)
class ErrorData(
    @SerializedName("code")
    val code: Int? = 0,
    @SerializedName("message")
    val message: String? = ""
)