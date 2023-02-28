package org.evidyaloka.core.student.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StudyPreference(
    val days: List<String> = listOf(),
    val slots: List<StudyTimeConfiguration> = listOf()
) : Parcelable
