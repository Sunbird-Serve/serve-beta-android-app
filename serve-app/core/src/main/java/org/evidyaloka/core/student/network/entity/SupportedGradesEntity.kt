package org.evidyaloka.core.student.network.entity

import com.google.gson.annotations.SerializedName

data class SupportedGradesEntity(
    @SerializedName("from")
    val from: Int? = 0,
    @SerializedName("to")
    val to: Int? = 0
)