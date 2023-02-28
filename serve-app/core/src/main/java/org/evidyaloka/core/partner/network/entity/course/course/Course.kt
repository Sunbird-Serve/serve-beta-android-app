package org.evidyaloka.core.partner.network.entity.course.course


import com.google.gson.annotations.SerializedName

data class Course(
    @SerializedName("grade")
    val grade: String? = "",
    @SerializedName("id")
    val id: Int? = 0,
    @SerializedName("name")
    val name: String? = ""
)