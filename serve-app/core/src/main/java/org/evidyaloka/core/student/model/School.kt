package org.evidyaloka.core.student.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class School(
    val id: Int = 0,
    val logoUrl: String = "",
    val name: String = "",
    val boardCode: String = "",
    val boardName: String = "",
    val status: String = ""
):Parcelable