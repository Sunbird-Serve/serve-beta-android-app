package org.evidyaloka.core.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
    data class SubTopic(
        val id: Int = 0,
        val subtopicName: String = "",
        val hasViewed: Boolean = false
    ) : Parcelable