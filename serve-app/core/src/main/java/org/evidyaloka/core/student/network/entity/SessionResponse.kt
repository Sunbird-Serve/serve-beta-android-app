package org.evidyaloka.core.student.network.entity


import com.google.gson.annotations.SerializedName

data class SessionResponse(
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
        @SerializedName("timeTableId")
        val timeTableId: Int? = 0,
        @SerializedName("timetableStatus")
        val timetableStatus: String? = ""
    ) {
        data class Session(
            @SerializedName("calDate")
            val calDate: String? = "",
            @SerializedName("classType")
            val classType: String? = "",
            @SerializedName("dayOfWeek")
            val dayOfWeek: Int? = 0,
            @SerializedName("endTime")
            val endTime: String? = "",
            @SerializedName("hasAttended")
            val hasAttended: Int? = 0,
            @SerializedName("id")
            val id: Int? = 0,
            @SerializedName("liveClassUrl")
            val liveClassUrl: String? = "",
            @SerializedName("offeringId")
            val offeringId: Int? = 0,
            @SerializedName("startTime")
            val startTime: String? = "",
            @SerializedName("status")
            val status: String? = "",
            @SerializedName("subjectName")
            val subjectName: String? = "",
            @SerializedName("subtopicIdStr")
            val subtopicIdStr: String? = "",
            @SerializedName("teacherName")
            val teacherName: String? = "",
            @SerializedName("topicId")
            val topicId: Int? = 0,
            @SerializedName("topicName")
            val topicName: String? = null,
            @SerializedName("videoLink")
            val videoLink: String? = null,
            @SerializedName("lngName")
            val lngName: String? = null,
            @SerializedName("lngCode")
            val lngCode: String? = null
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