package org.evidyaloka.core.partner.network.entity.student


import com.google.gson.annotations.SerializedName

data class StudentResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: String,
    @SerializedName("statusCode")
    val statusCode: Int
){
    data class Data(
            @SerializedName("students")
            val studentEntity: StudentEntity
    )
}