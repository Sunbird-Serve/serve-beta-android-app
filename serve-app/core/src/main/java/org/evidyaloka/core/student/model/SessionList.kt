package org.evidyaloka.core.student.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.evidyaloka.core.model.Session

/**
 * @author Madhankumar
 * created on 22-03-2021
 *
 */
@Parcelize
data class SessionList(
    val count: Int = 0,
    val timeTableId: Int = 0,
    val timetableStatus: String = "",
    val session: List<Session> = listOf()
) : Parcelable