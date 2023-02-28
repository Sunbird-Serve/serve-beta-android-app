package org.evidyaloka.core.student.database.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class CourseContentEntity(
    @PrimaryKey
    val id: Long = 0,
    var studentId: Int? = 0,
    val contentId: Int? = 0,
    val subtopicId: Int? = 0,
    val timetableId: Int? = 0,
    val topicId: Int? = 0,
    val offeringId: Int? = 0,
    val sessionId: Int? = 0,
    val title: String? = "",
    val subtitle: String? = "",
    val topicName: String? = "",
    val subTopicName: String? = "",
    val type: String? = "",
    val contentType: String? = "",
    var viewStatus: String? = "",
    val progress: Int? = 0,
    val mimeType: String? = "",
    val url: String? = "",
    val duration: Int? = 0,
    var localUrl: String?,
    val downloadStatus: Int? = 0,
    val reason: String? = ""
) : Parcelable
