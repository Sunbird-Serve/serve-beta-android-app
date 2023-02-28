package org.evidyaloka.core.partner.network.entity

import com.google.gson.annotations.SerializedName
import org.evidyaloka.core.partner.network.entity.rtc.ActivityEntity
import org.evidyaloka.core.partner.network.entity.rtc.TextbookEntity
import org.evidyaloka.core.partner.network.entity.rtc.VideoEntity
import org.evidyaloka.core.partner.network.entity.rtc.WorksheetEntity

data class CoursePlayerResponse(
    @SerializedName("data")
    val `data`: Data = Data(),
    @SerializedName("status")
    val status: String? = "",
    @SerializedName("statusCode")
    val statusCode: Int? = 0
) {
    data class Data(
        @SerializedName("hasAttended")
        val hasAttended: Int? = 0,
        @SerializedName("hasLiked")
        val hasLiked: Int? = 0,
        @SerializedName("contentDetail")
        val contentDetail: ContentDetail = ContentDetail()
    ) {
        data class ContentDetail(
            @SerializedName("activities")
            val activityEntities: List<ActivityEntity> = listOf(),
            @SerializedName("textbook")
            val textbookEntity: List<TextbookEntity> = listOf(),
            @SerializedName("videos")
            val videoEntities: List<VideoEntity> = listOf(),
            @SerializedName("worksheets")
            val worksheetEntities: List<WorksheetEntity> = listOf()
        )
    }
}
