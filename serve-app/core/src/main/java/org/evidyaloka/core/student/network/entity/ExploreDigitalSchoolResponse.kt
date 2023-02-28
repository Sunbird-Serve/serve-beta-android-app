package org.evidyaloka.core.student.network.entity
import com.google.gson.annotations.SerializedName

data class ExploreDigitalSchoolResponse(
    @SerializedName("data")
    val `data`: Data? = Data(),
    @SerializedName("status")
    val status: String? = "",
    @SerializedName("statusCode")
    val statusCode: Int? = 0
) {
    data class Data(
        @SerializedName("schools")
        val schools: List<School?>? = listOf()
    ) {
        data class School(
            @SerializedName("bannerUrl")
            val bannerUrl: String? = "",
            @SerializedName("courses")
            val courses: List<Course?>? = listOf(),
            @SerializedName("description")
            val description: String? = "",
            @SerializedName("dsmId")
            val dsmId: Int? = 0,
            @SerializedName("dsmName")
            val dsmName: String? = "",
            @SerializedName("enrolledStudentCount")
            val enrolledStudentCount: Int? = 0,
            @SerializedName("id")
            val id: Int? = 0,
            @SerializedName("medium")
            val medium: String? = "",
            @SerializedName("name")
            val name: String? = "",
            @SerializedName("partnerId")
            val partnerId: Int? = 0,
            @SerializedName("partnerName")
            val partnerName: String? = "",
            @SerializedName("schoolLogoUrl")
            val schoolLogoUrl: String? = "",
            @SerializedName("statementOfPurpose")
            val statementOfPurpose: String? = "",
            @SerializedName("status")
            val status: String? = ""
        ) {
            data class Course(
                @SerializedName("courseId")
                val courseId: Int? = 0,
                @SerializedName("name")
                val name: String? = "",
                @SerializedName("offeringId")
                val offeringId: Int? = 0,
                @SerializedName("languageName")
                val languageName: String? = "",
                @SerializedName("langaugeId")
                val langaugeId: Int? = 0
            )
        }
    }
}