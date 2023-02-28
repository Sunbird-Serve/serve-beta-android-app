package org.evidyaloka.core.student.network.mapper

import org.evidyaloka.core.Constants.StudentConst
import org.evidyaloka.core.helper.EntityMapper
import org.evidyaloka.core.model.rtc.ContentAttributes
import org.evidyaloka.core.model.rtc.*
import org.evidyaloka.core.partner.network.entity.rtc.ActivityEntity
import org.evidyaloka.core.partner.network.entity.rtc.TextbookEntity
import org.evidyaloka.core.partner.network.entity.rtc.VideoEntity
import org.evidyaloka.core.partner.network.entity.rtc.WorksheetEntity
import org.evidyaloka.core.student.network.entity.CoursePlayerResponse
import javax.inject.Inject

class CoursePlayerResponseMapper @Inject
constructor() : EntityMapper<CoursePlayerResponse, ContentDetail> {
    override fun mapFromEntity(entity: CoursePlayerResponse): ContentDetail {
        return ContentDetail(
            hasAttended = entity.data.hasAttended == 1,
            hasLiked = entity.data.hasLiked == 1,
            activities = mapActivities(entity.data.contentDetail.activityEntities),
            textbooks = mapTextbooks(entity.data.contentDetail.textbookEntity),
            videos = mapVideos(entity.data.contentDetail.videoEntities),
            worksheets = mapWorksheets(entity.data.contentDetail.worksheetEntities)
        )

    }

    override fun mapToEntity(domainModel: ContentDetail): CoursePlayerResponse {
        return CoursePlayerResponse()
    }

    fun mapVideos(sessions: List<VideoEntity>): MutableList<ContentAttributes> {
        return mutableListOf<ContentAttributes>().apply {
            sessions.forEach {
                add(
                    ContentAttributes(
                        id = it.id?:0,
                        progress = it.progress,
                        duration = it.duration,
                        url = it.url,
                        title = it.title,
                        description = it.description,
                        contentHost = it.contentHost,
                        contentType = it.contentType,
                        author = it.author,
                        subtopicId = it.subTopicId?:0,
                        thumbnailUrl = it.thumbnailUrl,
                        viewStatus = it.viewStatus?.takeIf { it.isNotBlank() || it.isNotEmpty() }?.toInt()?:null,
                        type = it.type?.let { if (it==0) {
                            StudentConst.MAIN_VIDEO
                        } else StudentConst.SUPPLEMENTARY_VIDEO} ?: StudentConst.SUPPLEMENTARY_VIDEO
                    )
                )
            }
        }
    }

    fun mapTextbooks(textbooks: List<TextbookEntity>): MutableList<ContentAttributes> {
        return mutableListOf<ContentAttributes>().apply {
            textbooks.forEach {
                add(
                    ContentAttributes(
                        id = it.id?:0,
                        author = it.author,
                        title = it.title,
                        contentHost = it.contentHost,
                        contentType = it.contentType,
                        viewStatus = it.viewStatus?.takeIf { it.isNotBlank() || it.isNotEmpty() }?.toInt()?:null,
                        url = it.url
                    )
                )
            }
        }
    }

    fun mapActivities(activities: List<ActivityEntity>): MutableList<ContentAttributes> {
        return mutableListOf<ContentAttributes>().apply {
            activities.forEach {
                add(
                    ContentAttributes(
                        id = it.id?:0,
                        author = it.author,
                        title = it.title,
                        contentHost = it.contentHost,
                        contentType = it.contentType,
                        viewStatus = it.viewStatus?.takeIf { it.isNotBlank() || it.isNotEmpty() }?.toInt()?:null,
                        url = it.url
                    )
                )
            }
        }
    }

    fun mapWorksheets(worksheets: List<WorksheetEntity>): MutableList<ContentAttributes> {
        return mutableListOf<ContentAttributes>().apply {
            worksheets.forEach {
                add(
                    ContentAttributes(
                        id = it.id?:0,
                        author = it.author,
                        title = it.title,
                        contentHost = it.contentHost,
                        contentType = it.contentType,
                        viewStatus = it.viewStatus?.takeIf { it.isNotBlank() || it.isNotEmpty() }?.toInt()?:null,
                        url = it.url
                    )
                )
            }
        }
    }

}