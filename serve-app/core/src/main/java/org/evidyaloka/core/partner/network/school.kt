package org.evidyaloka.core.partner.network


import com.google.gson.annotations.SerializedName

data class school(
    @SerializedName("bannerId")
    val bannerId: Any? = null,
    @SerializedName("bannerUrl")
    val bannerUrl: Any? = null,
    @SerializedName("centerId")
    val centerId: Any? = null,
    @SerializedName("courseCount")
    val courseCount: Int = 0,
    @SerializedName("createdOn")
    val createdOn: String = "",
    @SerializedName("description")
    val description: String = "",
    @SerializedName("dsmFirstName")
    val dsmFirstName: Any? = null,
    @SerializedName("dsmId")
    val dsmId: Any? = null,
    @SerializedName("dsmLastName")
    val dsmLastName: Any? = null,
    @SerializedName("dsmUsername")
    val dsmUsername: Any? = null,
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("logoId")
    val logoId: Int = 0,
    @SerializedName("logoUrl")
    val logoUrl: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("purpose")
    val purpose: String = "",
    @SerializedName("status")
    val status: String = "",
    @SerializedName("studentCount")
    val studentCount: Int = 0,
    @SerializedName("students")
    val students: List<Student> = listOf()
) {
    data class Student(
        @SerializedName("id")
        val id: Int = 0,
        @SerializedName("name")
        val name: String = "",
        @SerializedName("profileUrl")
        val profileUrl: String = ""
    )
}