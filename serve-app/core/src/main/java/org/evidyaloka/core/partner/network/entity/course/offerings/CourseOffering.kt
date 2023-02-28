package org.evidyaloka.core.partner.network.entity.course.offerings

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CourseOffering(
    @SerializedName("StartDate")
    val StartDate: String? = "",
    @SerializedName("EndDate")
    val EndDate: String? = "",
    @SerializedName("CourseProvider")
    val CourseProvider: String? = "",
    @SerializedName("id")
    val id: Int? = 0,
    @SerializedName("AcademicYear")
    val AcademicYear: String? = "",
    @SerializedName("courseName")
    val courseName: String? = "",
    @SerializedName("grade")
    val grade: String? = ""
) : Parcelable