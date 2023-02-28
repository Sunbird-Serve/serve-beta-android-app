package org.evidyaloka.core.student.database.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


@Entity
@Parcelize
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subtopicId: Int? = 0,
    val timetableId: Int? = 0,
    val topicId: Int? = 0,
    val offeringId: Int? = 0,
    val sessionId: Int? = 0,
    val topicName: String? = "",
    val subTopicName: String? = "",
    var studentId: Int? = 0
) : Parcelable
