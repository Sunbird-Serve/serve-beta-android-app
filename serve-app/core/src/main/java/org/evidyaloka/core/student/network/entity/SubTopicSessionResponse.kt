package org.evidyaloka.core.student.network.entity


import com.google.gson.annotations.SerializedName

data class SubTopicSessionResponse(
    @SerializedName("data")
    var `data`: Data? = null,
    @SerializedName("status")
    var status: String = "",
    @SerializedName("statusCode")
    var statusCode: Int = 0
) {
    data class Data(
        @SerializedName("offeringId")
        var offeringId: Int = 0,
        @SerializedName("sessionId")
        var sessionId: Int = 0,
        @SerializedName("subtopicId")
        var subtopicId: Int = 0,
        @SerializedName("timetableId")
        var timetableId: Int = 0,
        @SerializedName("topicId")
        var topicId: Int = 0
    )
}