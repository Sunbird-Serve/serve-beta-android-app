package org.evidyaloka.core.student.network.entity


import com.google.gson.annotations.SerializedName

data class SubjectResponse(
    @SerializedName("data")
    val `data`: Data = Data(),
    @SerializedName("status")
    val status: String? = "",
    @SerializedName("statusCode")
    val statusCode: Int? = 0
) {
    data class Data(
        @SerializedName("subjects")
        val subjects: List<Subject> = listOf()
    ) {
        data class Subject(
            @SerializedName("courseId")
            val courseId: Int? = 0,
            @SerializedName("offeringId")
            val offeringId: Int? = 0,
            @SerializedName("subjectName")
            val subjectName: String? = "",
            @SerializedName("totalTopics")
            val totalTopics: Int? = 0,
            @SerializedName("totalTopicsViewed")
            val totalTopicsViewed: Int? = 0
        )
    }
}