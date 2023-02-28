package org.evidyaloka.core.student.network.entity.rtc


import com.google.gson.annotations.SerializedName

data class ActivityEntity(
    @SerializedName("author")
    val author: String? = "",
    @SerializedName("contentHost")
    val contentHost: String? = "",
    @SerializedName("contentType")
    val contentType: String? = "",
    @SerializedName("id")
    val id: Int? = 0,
    @SerializedName("title")
    val title: String? = "",
    @SerializedName("description")
    val description: String? = "",
    @SerializedName("url")
    val url: String? = "",
    @SerializedName("viewStatus")
    val viewStatus: String? = ""
)