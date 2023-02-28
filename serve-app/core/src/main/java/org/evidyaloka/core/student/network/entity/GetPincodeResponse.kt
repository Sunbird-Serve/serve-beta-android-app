package org.evidyaloka.core.student.network.entity


import com.google.gson.annotations.SerializedName

data class GetPincodeResponse(
    @SerializedName("data")
    val `data`: Data = Data(),
    @SerializedName("status")
    val status: String = "",
    @SerializedName("statusCode")
    val statusCode: Int = 0
) {
    data class Data(
        @SerializedName("pincodes")
        val pincodes: List<PincodeEntity> = listOf()
    )
}