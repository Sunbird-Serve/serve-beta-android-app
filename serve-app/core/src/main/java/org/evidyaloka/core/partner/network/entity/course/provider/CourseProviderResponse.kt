package org.evidyaloka.core.partner.network.entity.course.provider

import com.google.gson.annotations.SerializedName

data class CourseProviderResponse(
        @SerializedName("data")
        val data: Data = Data(),
        @SerializedName("status")
        val status: String? = "",
        @SerializedName("statusCode")
        val statusCode: Int? = 0
) {
    data class Data(
            @SerializedName("courseProviders")
            val courseProviderList: List<CourseProvider> = ArrayList()
    )
}