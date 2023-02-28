package org.evidyaloka.core.partner.network.entity.course.offerings

import com.google.gson.annotations.SerializedName

data class CourseOfferingResponse(
        @SerializedName("data")
        val data: Offerings = Offerings(),
        @SerializedName("status")
        val status: String? = "",
        @SerializedName("statusCode")
        val statusCode: Int? = 0
) {
    data class Offerings(
            @SerializedName("offerings")
            val offerings: List<CourseOffering> = ArrayList()
    )
}