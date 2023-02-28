package org.evidyaloka.core.student.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @author Madhankumar
 * created on 22-03-2021
 *
 */
@Parcelize
data class DoubtList(
    val totalCount: Int? = 0,
    val doubts: List<Doubt> = listOf()
) : Parcelable