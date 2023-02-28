package org.evidyaloka.core.partner.network.entity


import com.google.gson.annotations.SerializedName

data class CheckCourseResponse(
    @SerializedName("data")
    var `data`: Data? = null,
    @SerializedName("status")
    var status: String? = "",
    @SerializedName("statusCode")
    var statusCode: Int = 0
) {
    data class Data(
        @SerializedName("isSimilarOfferingExist")
        var isSimilarOfferingExist: Int = 0
    )
}