package org.evidyaloka.core.partner.network.entity


import com.google.gson.annotations.SerializedName

data class StateListResponse(
    @SerializedName("data")
    val `data`: Data? = null,
    @SerializedName("status")
    val status: String? = null,
    @SerializedName("statusCode")
    val statusCode: Int? = null
) {
    data class Data(
        @SerializedName("states")
        val states: List<StateEntity> = listOf()
    )
}