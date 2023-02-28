package org.evidyaloka.core.student.network.entity.doubt

import com.google.gson.annotations.SerializedName

data class DoubtEntity(
    @SerializedName("contentType")
    val contentType: String? = "",
    @SerializedName("courseName")
    val courseName: String? = "",
    @SerializedName("recordType")
    val recordType: String? ="",
    @SerializedName("resourceType")
    val resourceType: String? ="",
    @SerializedName("createdDate")
    val createdDate: String? ="",
    @SerializedName("id")
    val id: Int? = 0,
    @SerializedName("offeringId")
    val offeringId: Int? = 0,
    @SerializedName("status")
    val status: String?= "",
    @SerializedName("subTopicId")
    val subTopicId: Int?= 0,
    @SerializedName("subTopicName")
    val subTopicName: String?= "",
    @SerializedName("topicId")
    val topicId: Int?= 0,
    @SerializedName("topicName")
    val topicName: String?= "",
    @SerializedName("url")
    val url: String?= "",
    @SerializedName("teacherName")
    val teacherName: String? = "",
    @SerializedName("text")
    val text: String = "",
    @SerializedName("contentHost")
    val contentHost: String = "",
    @SerializedName("note")
    val note: String = "",
    @SerializedName("thumbnailUrl")
    val thumbnailUrl: String = ""
)