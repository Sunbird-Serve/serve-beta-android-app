package org.evidyaloka.core.student.network.entity

import com.google.gson.annotations.SerializedName

class MissedClassesResponse (
    @SerializedName("data")
    val data: Data = Data(),
    @SerializedName("status")
    val status: String? = "",
    @SerializedName("statusCode")
    val statusCode: Int? = 0
) {
    class Data(
        @SerializedName("count")
        val count: Int? = 0,
        @SerializedName("firstMissedDate")
        val firstMissedDate: String? = ""
    )
}