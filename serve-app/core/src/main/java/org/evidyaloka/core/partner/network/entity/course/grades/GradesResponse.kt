package org.evidyaloka.core.partner.network.entity.course.grades

import com.google.gson.annotations.SerializedName

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