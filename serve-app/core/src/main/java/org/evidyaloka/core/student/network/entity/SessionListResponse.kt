package org.evidyaloka.core.student.network.entity


import com.google.gson.annotations.SerializedName

data class SessionListResponse(
    @SerializedName("data")
    val `data`: Data = Data(),
    @SerializedName("status")
    val status: String? = "",
    @SerializedName("statusCode")
    val statusCode: Int? = 0
) {
    data class Data(
        @SerializedName("count")
        val count: Int? = 0,
        @SerializedName("sessions")
        val sessions: List<Session> = listOf(),
        @SerializedName("timetableStatus")
        val timetableStatus: String? = ""
    ) {
        data class Session(
            @SerializedName("author")
            val author: String? = "",
            @SerializedName("classType")
            val classType: String? = "",
            @SerializedName("date")
            val date: String? = "",
            @SerializedName("endTime")
            val endTime: String? = "",
            @SerializedName("hasAttended")
            val hasAttended: Int? = 0,
            @SerializedName("id")
            val id: Int? = 0,
            @SerializedName("liveClassUrl")
            val liveClassUrl: Any? = null,
            @SerializedName("offeringId")
            val offeringId: Int? = 0,
            @SerializedName("startTime")
            val startTime: String? = "",
            @SerializedName("subTopics")
            val subTopics: List<SubTopic> = listOf(),
            @SerializedName("subjectName")
            val subjectName: String? = "",
            @SerializedName("teacherName")
            val teacherName: String? = "",
            @SerializedName("topic")
            val topic: String? = "",
            @SerializedName("topicId")
            val topicId: Int? = 0,
            @SerializedName("topicName")
            val topicName: String? = ""
        ) {
            data class SubTopic(
                @SerializedName("id")
                val id: Int? = 0,
                @SerializedName("subtopicName", alternate = ["name"])
                val subtopicName: String? = ""
            )
        }
    }
}