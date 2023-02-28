package org.evidyaloka.core.student.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StudyTimeConfiguration(
    val displayName: String = "",
    val endTime: Int = 0,
    val key: String = "",
    val startTime: Int = 0,
    val startTimeMin: Int = 0,
    val endTimeMin: Int = 0

): Parcelable