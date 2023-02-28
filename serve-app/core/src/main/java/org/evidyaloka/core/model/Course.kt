package org.evidyaloka.core.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @author Madhankumar
 * created on 16-03-2021
 *
 */
@Parcelize
data class Course(
    val id: Int = 0,
    val name: String = "",
    var isSelected: Boolean = false,
    val offeringId: Int = 0,
    val grade: String = "",
    val languageName: String = "",
    val langaugeId: Int = 0
) : Parcelable