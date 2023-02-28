package org.evidyaloka.core.student.network.entity

import com.google.gson.annotations.SerializedName
import org.evidyaloka.core.student.network.entity.StudyTimeConfiguration

data class StudyPreferenceResponse(
    @SerializedName("data")
    val `data`: Data = Data(),
    @SerializedName("status")
    val status: String = "",
    @SerializedName("statusCode")
    val statusCode: Int = 0
) {
    data class Data(
        @SerializedName("days")
        val days: List<String> = listOf(),
        @SerializedName("id")
        val id: String = "",
        @SerializedName("slots")
        val slots: List<StudyTimeConfiguration> = listOf()
    )
}