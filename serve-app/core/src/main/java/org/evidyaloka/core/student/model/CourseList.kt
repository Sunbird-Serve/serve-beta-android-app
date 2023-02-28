package org.evidyaloka.core.student.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.evidyaloka.core.model.Course

@Parcelize
data class CourseList(
    val courses: List<Course> = listOf()
) : Parcelable