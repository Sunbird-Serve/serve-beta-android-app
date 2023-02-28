package org.evidyaloka.core.student.network.entity

import com.google.gson.annotations.SerializedName


data class PingResponse(
    @SerializedName("data")
    val `data`: Data = Data(),
    @SerializedName("status")
    val status: String? = "",
    @SerializedName("statusCode")
    val statusCode: Int? = 0
) {
    data class Data(
        @SerializedName("sessionId")
        val sessionId: String? = null,
        @SerializedName("sessionExpiryTime")
        val sessionExpiryTime: String? = null,
        @SerializedName("settings")
        val settings: Settings = Settings(),
        @SerializedName("userId")
        val userId: Int? = 0,
        @SerializedName("userStatus")
        val userStatus: String? = ""
    ) {
        data class Settings(
            @SerializedName("otpExpiryTime")
            val otpExpiryTime: String? = "",
            @SerializedName("studyTimeConfiguration")
            val studyTimeConfiguration: List<StudyTimeConfiguration> = listOf(),
            @SerializedName("allowedChapterForNewUser")
            val allowedChapterForNewUser: AllowedChapterForNewUserEntity = AllowedChapterForNewUserEntity(),
            @SerializedName("supportedGrades")
            val supportedGrades: SupportedGradesEntity = SupportedGradesEntity()

        ) {
            data class AllowedChapterForNewUserEntity(
                @SerializedName("parent")
                val parent: ContentSettingsEntity = ContentSettingsEntity(),
                @SerializedName("partner")
                val partner: ContentSettingsEntity = ContentSettingsEntity()
            ) {
                data class ContentSettingsEntity(
                    @SerializedName("numberOfChapters")
                    val numberOfChapters: String? = "",
                    @SerializedName("numberOfSubtopicAllowedPerChapter")
                    val numberOfSubtopicAllowedPerChapter: String? = ""
                )
            }
        }
    }
}