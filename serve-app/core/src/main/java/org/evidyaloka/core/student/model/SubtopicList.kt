package org.evidyaloka.core.student.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.evidyaloka.core.model.SubTopic

/**
 * @author Madhankumar
 * created on 22-03-2021
 *
 */
@Parcelize
data class SubtopicList(
    val sessionId: String = "",
    val subtopics: List<SubTopic> = listOf()
) : Parcelable