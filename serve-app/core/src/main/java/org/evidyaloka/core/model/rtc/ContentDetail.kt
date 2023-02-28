package org.evidyaloka.core.model.rtc

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ContentDetail(
    var hasAttended: Boolean = false,
    val hasLiked: Boolean = false,
    val activities: MutableList<ContentAttributes> = mutableListOf(),
    val worksheets: MutableList<ContentAttributes> = mutableListOf(),
    var videos: MutableList<ContentAttributes> = mutableListOf(),
    val textbooks: MutableList<ContentAttributes> = mutableListOf()
): Parcelable