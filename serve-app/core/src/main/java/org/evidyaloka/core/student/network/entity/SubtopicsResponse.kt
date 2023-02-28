package org.evidyaloka.core.student.network.entity


import com.google.gson.annotations.SerializedName

data class SubtopicsResponse(
    @SerializedName("data")
    val `data`: Data = Data(),
    @SerializedName("status")
    val status: String? = "",
    @SerializedName("statusCode")
    val statusCode: Int? = 0
) {
    data class Data(
        @SerializedName("sessionId")
        val sessionId: String? = "",
        @SerializedName("subtopics")
        val subtopics: List<SessionResponse.Data.Session.SubTopic> = listOf()
    )
}