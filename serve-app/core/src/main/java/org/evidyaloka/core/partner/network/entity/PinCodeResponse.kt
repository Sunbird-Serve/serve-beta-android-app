package org.evidyaloka.core.partner.network.entity


import com.google.gson.annotations.SerializedName

data class PinCodeResponse(
    @SerializedName("data")
    val `data`: Data? = null,
    @SerializedName("status")
    val status: String? = null,
    @SerializedName("statusCode")
    val statusCode: Int? = null
) {
    data class Data(
        @SerializedName("pincodes")
        val pincodeEntities: List<PincodeEntity>?
    )
}