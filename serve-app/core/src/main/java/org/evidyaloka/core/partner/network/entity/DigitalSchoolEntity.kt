package org.evidyaloka.core.partner.network.entity

import com.google.gson.annotations.SerializedName
import org.evidyaloka.core.model.CourseProvider
import org.evidyaloka.core.partner.model.DigitalSchool

/**
 * @author Madhankumar
 * created on 08-01-2021
 *
 */
data class DigitalSchoolEntity(
        @SerializedName("bannerId")
        val bannerId: Int? = null,
        @SerializedName("bannerUrl")
        val bannerUrl: String? = null,
        @SerializedName("centerId")
        val centerId: Int? = null,
        @SerializedName("courseCount")
        val courseCount: Int? = 0,
        @SerializedName("createdOn")
        val createdOn: String? = "",
        @SerializedName("description")
        val description: String? = "",
        @SerializedName("dsmFirstName")
        val dsmFirstName: String? = null,
        @SerializedName("dsmId")
        val dsmId: Int? = null,
        @SerializedName("dsmLastName")
        val dsmLastName: String? = null,
        @SerializedName("dsmUsername")
        val dsmUsername: String? = null,
        @SerializedName("id")
        val id: Int? = 0,
        @SerializedName("logoId")
        val logoId: Int? = 0,
        @SerializedName("logoUrl")
        val logoUrl: String? = "",
        @SerializedName("name",alternate = ["digitalSchoolName"])
        val name: String? = "",
        @SerializedName("purpose")
        val purpose: String? = "",
        @SerializedName("status")
        val status: String? = "",
        @SerializedName("studentCount")
        val studentCount: Int? = 0,
        @SerializedName("students")
        val students: List<DigitalSchool.Student> = listOf(),
        @SerializedName("selectedStates")
        val selectedStates: List<StateEntity> = listOf(),
        @SerializedName("courseProviders")
        val courseProviderList: List<CourseProvider>? = ArrayList()
) {
    data class Student(
            @SerializedName("id")
            val id: Int? = 0,
            @SerializedName("name")
            val name: String? = "",
            @SerializedName("profileUrl")
            val profileUrl: String? = ""
    )
}