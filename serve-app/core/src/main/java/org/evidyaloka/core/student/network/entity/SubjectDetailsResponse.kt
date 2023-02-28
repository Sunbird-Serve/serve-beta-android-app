package org.evidyaloka.core.student.network.entity


import com.google.gson.annotations.SerializedName

data class SubjectDetailsResponse(
    @SerializedName("data")
    val `data`: Data = Data(),
    @SerializedName("status")
    val status: String? = "",
    @SerializedName("statusCode")
    val statusCode: Int? = 0
) {
    data class Data(
        @SerializedName("topics")
        val topics: List<Topic> = listOf()
    ) {
        data class Topic(
            @SerializedName("id")
            val id: Int? = 0,
            @SerializedName("name")
            val name: String? = "",
            @SerializedName("subtopics")
            val subtopics: List<Subtopic> = listOf()
        ) {
            data class Subtopic(
                @SerializedName("hasViewed")
                val hasViewed: Int? = 0,
                @SerializedName("id")
                val id: Int? = 0,
                @SerializedName("name")
                val name: String? = ""
            )
        }
    }
}