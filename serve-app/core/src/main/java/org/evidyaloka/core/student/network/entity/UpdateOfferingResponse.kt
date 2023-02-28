package org.evidyaloka.core.student.network.entity


import com.google.gson.annotations.SerializedName

data class UpdateOfferingResponse(
    @SerializedName("data")
    val `data`: Data = Data(),
    @SerializedName("status")
    val status: String? = "",
    @SerializedName("statusCode")
    val statusCode: Int? = 0
) {
    data class Data(
        @SerializedName("isUpdateScheduleRequired")
        val isUpdateScheduleRequired: Int = 0,
        @SerializedName("message")
        val message: String? = ""
    )
}