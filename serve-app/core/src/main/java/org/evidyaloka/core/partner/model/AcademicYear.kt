package org.evidyaloka.core.partner.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AcademicYear(
        val academicYear: String = "",
        val id: Int = 0
):Parcelable