package org.evidyaloka.core.student.network.entity

import com.google.gson.annotations.SerializedName

class EditScheduleResponse(
    @SerializedName("data")
    val data: Data = Data(),
    @SerializedName("status")
    val status: String? = "",
    @SerializedName("statusCode")
    val statusCode: Int? = 0
) {
    class Data(
        @SerializedName("isUpdateScheduleRequired", alternate = ["id"])
        val id: Int? = 0,
        @SerializedName("message", alternate = ["Message"])
        val message: String? = ""
    )

}