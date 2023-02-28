package org.evidyaloka.core.partner.network.entity.student


import com.google.gson.annotations.SerializedName

data class StudentEntity(
    @SerializedName("dob")
    val dob: String?,
    @SerializedName("gender")
    val gender: String?,
    @SerializedName("grade")
    val grade: String?,
    @SerializedName("guardianName")
    val guardianName: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("mediumOfStudy")
    val mediumOfStudy: MediumOfStudy,
    @SerializedName("mobile")
    val mobile: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("offeringsOpted")
    val offeringsOpted: List<OfferingsOpted>?,
    @SerializedName("physicalSchoolName")
    val physicalSchoolName: String?,
    @SerializedName("profileUrl")
    val profileUrl: String?,
    @SerializedName("relationshipType")
    val relationshipType: String?,
    @SerializedName("onboardingStatus")
    val onboardingStatus: String?,
    @SerializedName("status")
    val status: String?
)