package org.evidyaloka.core.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * @author Madhankumar
 * created on 14-04-2021
 *
 */
@Parcelize
data class Subject(
    val courseId: Int = 0,
    val offeringId: Int = 0,
    val subjectName: String = "",
    val totalTopics: Int = 0,
    val totalTopicsViewed: Int = 0
):Parcelable
