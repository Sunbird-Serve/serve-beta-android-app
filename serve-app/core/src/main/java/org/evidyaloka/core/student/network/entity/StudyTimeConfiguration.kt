package org.evidyaloka.core.student.network.entity

import com.google.gson.annotations.SerializedName

data class StudyTimeConfiguration(
    @SerializedName("DisplayName")
    val displayName: String? = "",
    @SerializedName("EndTime")
    val endTime: Int? = 0,
    @SerializedName("key")
    val key: String? = "",
    @SerializedName("StartTime")
    val startTime: Int? = 0,
    @SerializedName("startTimeMin")
    val startTimeMin: Int? = 0,
    @SerializedName("endTimeMin")
    val endTimeMin: Int? = 0
)
