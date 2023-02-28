package org.evidyaloka.core.student.network.entity


import com.google.gson.annotations.SerializedName

data class CourseProviderEntity(
    @SerializedName("code")
    val code: String ="",
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("name")
    val name: String = "",
    @SerializedName("type")
    val type: String =""
)