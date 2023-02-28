package org.evidyaloka.core.partner.network.entity.ping


import com.google.gson.annotations.SerializedName
import org.evidyaloka.core.student.network.entity.PingResponse

data class PingResponse(
    @SerializedName("data")
    val `data`: Data = Data(),
    @SerializedName("status")
    val status: String? = "",
    @SerializedName("statusCode")
    val statusCode: Int? = 0
) {
    data class Data(
        @SerializedName("partnerId")
        val partnerId: Int? = 0,
        @SerializedName("partnerStatus")
        val partnerStatus: String? = "",
        @SerializedName("sessionExpiryTime")
        val sessionExpiryTime: Int? = 0,
        @SerializedName("sessionId")
        val sessionId: String? = "",
        @SerializedName("settings")
        val settings: Settings = Settings(),
        @SerializedName("userId")
        val userId: Int? = 0,
        @SerializedName("useruserStatus", alternate = ["userStatus"])
        val useruserStatus: String? = ""
    ) {
        data class Settings(
            @SerializedName("supportedGrades")
            val supportedGrades: SupportedGrades = SupportedGrades(),
            @SerializedName("allowedChapterForNewUser")
            val allowedChapterForNewUser: AllowedChapterForNewUserEntity = AllowedChapterForNewUserEntity()
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