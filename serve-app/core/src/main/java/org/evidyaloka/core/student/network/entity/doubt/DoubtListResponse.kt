package org.evidyaloka.core.student.network.entity.doubt

import com.google.gson.annotations.SerializedName

data class DoubtListResponse(
    @SerializedName("data")
    val `data`: Data = Data(),
    @SerializedName("status")
    val status: String? = "",
    @SerializedName("statusCode")
    val statusCode: Int? = 0
) {
    data class Data(
        @SerializedName("totalCount")
        val totalCount: Int? = 0,
        @SerializedName("doubts")
        val doubts: List<DoubtEntity> = listOf()
    )
}