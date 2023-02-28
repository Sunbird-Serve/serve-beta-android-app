package org.evidyaloka.core.helper

import com.google.gson.annotations.SerializedName

/**
 * @author Madhankumar
 * created on 24-02-2021
 *
 */
class ErrorData(
    @SerializedName("code")
    val code: Int = 0,
    @SerializedName("message")
    val message: String = ""
)