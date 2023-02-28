package org.evidyaloka.core.partner.network.entity.student


import com.google.gson.annotations.SerializedName

data class MediumOfStudy(
    @SerializedName("languageId")
    val languageId: Int?,
    @SerializedName("name")
    val name: String?
)