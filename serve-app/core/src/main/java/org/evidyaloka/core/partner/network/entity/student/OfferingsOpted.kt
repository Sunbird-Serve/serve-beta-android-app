package org.evidyaloka.core.partner.network.entity.student


import com.google.gson.annotations.SerializedName

data class OfferingsOpted(
    @SerializedName("grade")
    val grade: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("subject")
    val subject: String?
)