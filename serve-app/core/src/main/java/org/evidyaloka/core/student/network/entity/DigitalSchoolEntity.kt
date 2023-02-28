package org.evidyaloka.core.student.network.entity

import com.google.gson.annotations.SerializedName

data class DigitalSchoolEntity(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("logoUrl", alternate = ["schoolLogoUrl"])
    val logoUrl: String?,
    @SerializedName("name", alternate = ["digitalSchoolName"])
    val name: String?,
    @SerializedName("boardCode")
    val boardCode: String?,
    @SerializedName("boardName")
    val boardName: String?,
    @SerializedName("status")
    val status: String?
)

