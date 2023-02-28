package org.evidyaloka.core.partner.network.entity.rtc


import com.google.gson.annotations.SerializedName

data class TextbookEntity(
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
    @SerializedName("url")
    val url: String? = "",
    @SerializedName("viewStatus")
    val viewStatus: String? = ""
)