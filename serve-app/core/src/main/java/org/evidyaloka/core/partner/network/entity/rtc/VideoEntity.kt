package org.evidyaloka.core.partner.network.entity.rtc


import com.google.gson.annotations.SerializedName

data class VideoEntity(
    @SerializedName("author")
    val author: String? = "",
    @SerializedName("contentHost")
    val contentHost: String? = "",
    @SerializedName("contentType")
    val contentType: String? = "",
    @SerializedName("duration")
    val duration: Int? = 0,
    @SerializedName("id")
    val id: Int? = 0,
    @SerializedName("progress")
    val progress: Int? = 0,
    @SerializedName("subTopicId")
    val subTopicId: Int? = 0,
    @SerializedName("thumbnailUrl")
    val thumbnailUrl: String? = "",
    @SerializedName("title")
    val title: String? = "",
    @SerializedName("description")
    val description: String? = "",
    @SerializedName("type")
    val type: Int? = 0,
    @SerializedName("url")
    val url: String? = "",
    @SerializedName("viewStatus")
    val viewStatus: String? = "",
    @SerializedName("isPrimary")
    val isPrimary: Boolean? = false
)