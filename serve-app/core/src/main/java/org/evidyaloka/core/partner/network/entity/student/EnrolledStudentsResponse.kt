package org.evidyaloka.core.partner.network.entity.student


import com.google.gson.annotations.SerializedName

data class EnrolledStudentsResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: String,
    @SerializedName("statusCode")
    val statusCode: Int
){
    data class Data(
            @SerializedName("students")
            val students: List<StudentEntity>,
            @SerializedName("totalCount")
            val count:Int? = null

    )
}