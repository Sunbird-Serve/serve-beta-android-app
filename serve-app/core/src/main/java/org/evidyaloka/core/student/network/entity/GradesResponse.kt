package org.evidyaloka.core.student.network.entity

import com.google.gson.annotations.SerializedName
import org.evidyaloka.core.student.network.LanguagesResponse

data class GradesResponse(
    @SerializedName("data")
    val data: Grade = Grade(),
    @SerializedName("status")
    val status: String? = "",
    @SerializedName("statusCode")
    val statusCode: Int? = 0
) {
    data class Grade(
        @SerializedName("grades")
        val grades: List<Int> = ArrayList()
    )
}