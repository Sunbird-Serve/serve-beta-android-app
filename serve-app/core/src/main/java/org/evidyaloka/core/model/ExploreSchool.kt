package org.evidyaloka.core.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.evidyaloka.core.model.Course

@Parcelize
data class ExploreSchool(
    val bannerUrl: String = "",
    val courses: List<Course> = listOf(),
    val description: String = "",
    val dsmId: Int = 0,
    val dsmName: String = "",
    val enrolledStudentCount: Int = 0,
    val id: Int = 0,
    val medium: String = "",
    val name: String = "",
    val partnerId: Int = 0,
    val partnerName: String = "",
    val schoolLogoUrl: String  = "",
    val statementOfPurpose: String = "",
    val status: String = ""
):Parcelable
