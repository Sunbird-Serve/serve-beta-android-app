package org.evidyaloka.core.student.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Doubt(
    val id: Int = 0,
    val offeringId: Int = 0,
    val status: String = "",
    val subjectName: String = "",
    val teacherName: String = "",
    val topicId: Int = 0,
    val topicName: String = "",
    val subTopicId: Int = 0,
    val recordType: String = "",
    val resourceType: String = "",
    val subTopicName: String = "",
    val contentType: String = "",
    val url: String = "",
    val createdDate: String = "",
    val text: String = "",
    val contentHost: String = "",
    val thumbnailUrl: String = ""
) : Parcelable