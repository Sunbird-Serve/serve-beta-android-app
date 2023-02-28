package org.evidyaloka.core.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @author Madhankumar
 * created on 22-03-2021
 *
 */
@Parcelize
data class Session(
    val calDate: String = "",
    val classType: String = "",
    val dayOfWeek: Int = 0,
    val endTime: String = "",
    val hasAttended: Boolean = false,
    val id: Int = 0,
    val liveClassUrl: String = "",
    val offeringId: Int = 0,
    val startTime: String = "",
    val status: String = "",
    val subjectName: String = "",
    val subtopicIdStr: String = "",
    val teacherName: String = "",
    val topicId: Int = 0,
    val topicName: String = "",
    val videoLink: String? = null,
    val lngCode: String? = null,
    val lngName: String? = null
) : Parcelable