package org.evidyaloka.core.partner.network.entity.course.course


import com.google.gson.annotations.SerializedName

data class CourseResponse(
    @SerializedName("data")
    val `data`: Data= Data(),
    @SerializedName("status")
    val status: String? ="",
    @SerializedName("statusCode")
    val statusCode: Int? = 0
){
    data class Data(
            @SerializedName("courses")
            val courses: List<Course> = ArrayList()
    )
}
