package org.evidyaloka.core.student.model


import com.google.gson.annotations.SerializedName

data class SubTopicSession(
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