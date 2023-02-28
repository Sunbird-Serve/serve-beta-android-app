package org.evidyaloka.core.partner.network.entity.course.academic

import com.google.gson.annotations.SerializedName

data class AcademicYear(
        @SerializedName("academicYear")
        val academicYear: String? = "",
        @SerializedName("id")
        val id: Int? = 0
)