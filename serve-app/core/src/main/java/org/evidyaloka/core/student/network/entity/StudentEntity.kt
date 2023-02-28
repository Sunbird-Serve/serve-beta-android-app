package org.evidyaloka.core.student.network.entity

import com.google.gson.annotations.SerializedName

data class StudentEntity(
    @SerializedName("dob")
    val dob: String?,
    @SerializedName("gender")
    val gender: String?,
    @SerializedName("grade", alternate = ["Grade"])
    val grade: String?,
    @SerializedName("guardianName")
    val guardianName: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("mobile")
    val mobile: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("physicalSchoolName")
    val physicalSchoolName: String?,
    @SerializedName("profileUrl", alternate = ["profileImageUrl"])
    val profileUrl: String?,
    @SerializedName("relationshipType")
    val relationshipType: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("onboardingStatus")
    val onboardingStatus: String?,
    @SerializedName("classStatus")
    var classStatus: Int? = null,
    @SerializedName("schoolAssigned")
    val schoolAssigned : DigitalSchoolEntity
 )