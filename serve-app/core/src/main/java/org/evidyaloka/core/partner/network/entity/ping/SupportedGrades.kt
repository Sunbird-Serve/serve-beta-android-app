package org.evidyaloka.core.partner.network.entity.ping


import com.google.gson.annotations.SerializedName

data class SupportedGrades(
    @SerializedName("from")
    val from: Int? = 0,
    @SerializedName("to")
    val to: Int? = 0
)