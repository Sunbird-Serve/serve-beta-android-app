package org.evidyaloka.core.partner.network.entity.course.provider

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CourseProvider(
        @SerializedName("code")
        val code: String? = "",
        @SerializedName("id")
        val id: Int? = 0,
        @SerializedName("name")
        val name: String? = "",
        @SerializedName("type")
        val type: String? = ""
):Parcelable