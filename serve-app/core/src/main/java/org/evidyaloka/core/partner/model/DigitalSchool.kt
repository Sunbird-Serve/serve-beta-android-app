package org.evidyaloka.core.partner.model

import android.net.Uri
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import org.evidyaloka.core.model.CourseProvider

/**
 * @author Madhankumar
 * created on 08-01-2021
 *
 */
@Parcelize
data class DigitalSchool constructor(
        @SerializedName("bannerId")
        val bannerId: Int? = null,
        @SerializedName("bannerUrl")
        val bannerUrl: String? = null,
        @SerializedName("centerId")
        val centerId: Int? = null,
        @SerializedName("courseCount")
        val courseCount: Int = 0,
        @SerializedName("createdOn")
        val createdOn: String = "",
        @SerializedName("description")
        val description: String = "",
        @SerializedName("dsmFirstName")
        val dsmFirstName: String? = null,
        @SerializedName("dsmId")
        val dsmId: Int? = null,
        @SerializedName("dsmLastName")
        val dsmLastName: String? = null,
        @SerializedName("dsmUsername")
        val dsmUsername: String? = null,
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
        val students: List<Student> = listOf(),
        var selectedState: MutableList<State> = mutableListOf(),
        val logoUri: Uri? = null,
        val bannerUri: Uri? = null,
        val courseProviderList: List<CourseProvider> = listOf()
): Parcelable{
        @Parcelize
    data class Student(
            @SerializedName("id")
            val id: Int = 0,
            @SerializedName("name")
            val name: String = "",
            @SerializedName("profileUrl")
            val profileUrl: String = ""
    ): Parcelable
}

