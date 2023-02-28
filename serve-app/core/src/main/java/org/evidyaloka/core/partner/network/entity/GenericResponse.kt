package org.evidyaloka.core.partner.network.entity


import com.google.gson.annotations.SerializedName

class GenericResponse(
        @SerializedName("data")
        val data: Data = Data(),
        @SerializedName("status")
        val status: String? = "",
        @SerializedName("statusCode")
        val statusCode: Int? = 0
) {
    class Data(
            @SerializedName("id")
            val id: String? = "",
            @SerializedName("message")
            val message: String? = ""
    )

}