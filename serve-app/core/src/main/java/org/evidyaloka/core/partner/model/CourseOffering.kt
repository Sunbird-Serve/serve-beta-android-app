package org.evidyaloka.core.partner.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CourseOffering(
        val id: Int = 0,
        val CourseProvider: String = "",
        val AcademicYear: String = "",
        val grade: String = "",
        val courseName: String = "",
        val StartDate: String = "",
        val EndDate: String = ""
): Parcelable