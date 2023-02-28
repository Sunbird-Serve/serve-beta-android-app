package org.evidyaloka.core.student.network.entity


import com.google.gson.annotations.SerializedName

data class OfferingResponse(
    @SerializedName("data")
    val data: Data = Data(),
    @SerializedName("status")
    val status: String? = "",
    @SerializedName("statusCode")
    val statusCode: Int? = 0
) {
    data class Data(
        @SerializedName("courses")
        val courses: List<Course> = listOf()
    ) {
        data class Course(
            @SerializedName("id")
            val id: Int? = 0,
            @SerializedName("name")
            val name: String? = "",
            @SerializedName("languageName")
            val languageName: String? = "",
            @SerializedName("langaugeId")
            val langaugeId: Int? = 0
        )
    }
}