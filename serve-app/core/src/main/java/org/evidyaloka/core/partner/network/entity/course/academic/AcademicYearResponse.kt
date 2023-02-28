package org.evidyaloka.core.partner.network.entity.course.academic

import com.google.gson.annotations.SerializedName

data class AcademicYearResponse(
        @SerializedName("data")
        val data: Data = Data(),
        @SerializedName("status")
        val status: String? = "",
        @SerializedName("statusCode")
        val statusCode: Int? = 0
) {
    data class Data(
            @SerializedName("academicYears")
            val academicYears: List<AcademicYear> = ArrayList()
    )
}