package org.evidyaloka.core.partner.network.entity


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SchoolsResponse(
        @Expose
        @SerializedName("data")
        val data: Data = Data(),
        @SerializedName("status")
        @Expose
        val status: String? = "",
        @Expose
        @SerializedName("statusCode")
        val statusCode: Int? = 0
) {
    class Data(
            @Expose
            @SerializedName("schools")
            val schools: List<DigitalSchoolEntity> = listOf()
    )
}