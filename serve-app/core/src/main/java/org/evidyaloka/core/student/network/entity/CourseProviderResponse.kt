package org.evidyaloka.core.student.network.entity


import com.google.gson.annotations.SerializedName

data class CourseProviderResponse(
    @SerializedName("data")
    val `data`: Data = Data(),
    @SerializedName("status")
    val status: String = "",
    @SerializedName("statusCode")
    val statusCode: Int = 0
) {
    data class Data(
        @SerializedName("courseProviders")
        val courseProviders: List<CourseProviderEntity> = listOf()
    )
}