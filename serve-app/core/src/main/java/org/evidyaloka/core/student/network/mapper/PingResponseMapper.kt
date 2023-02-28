package org.evidyaloka.core.student.network.mapper

import org.evidyaloka.core.helper.EntityMapper
import org.evidyaloka.core.model.ContentSettings
import org.evidyaloka.core.student.model.Settings
import org.evidyaloka.core.student.model.StudyTimeConfiguration
import org.evidyaloka.core.student.network.entity.PingResponse
import javax.inject.Inject

/**
 * @author Madhankumar
 * created on 18-03-2021
 *
 */
class PingResponseMapper @Inject
constructor() : EntityMapper<PingResponse, Settings> {
    override fun mapFromEntity(entity: PingResponse): Settings {
        entity?.data?.let {
            return Settings(
                it.userId ?: 0,
                it.sessionId ?: "",
                it.sessionExpiryTime ?: "",
                it.userStatus ?: "",
                it.settings.otpExpiryTime?.toLong() ?: 0,
                studyStudyTimeConfiguration = mapStudyTimeConfiguration(it.settings.studyTimeConfiguration),
                parentContentSettings = mapContentSettings(it.settings.allowedChapterForNewUser.parent),
                partnerContentSettings = mapContentSettings(it.settings.allowedChapterForNewUser.partner),
                gradeTo = it.settings?.supportedGrades?.to ?: 0,
                gradeFrom = it.settings?.supportedGrades.from ?: 0
            )
        }
    }

    fun mapStudyTimeConfiguration(entity: List<org.evidyaloka.core.student.network.entity.StudyTimeConfiguration>): List<StudyTimeConfiguration> {
        return mutableListOf<StudyTimeConfiguration>().apply {
            entity.forEach {
                add(
                    StudyTimeConfiguration(
                        it.displayName?:"",
                        it.endTime?:0,
                        it.key?:"",
                        it.startTime?:0,
                        it.startTimeMin?:0,
                        it.endTimeMin?:0
                    )
                )
            }
        }
    }

    fun mapContentSettings(entity: PingResponse.Data.Settings.AllowedChapterForNewUserEntity.ContentSettingsEntity): ContentSettings {
        return ContentSettings(
            entity.numberOfChapters?.takeIf { it.isNotEmpty() }?.toInt() ?: 0,
            entity.numberOfSubtopicAllowedPerChapter?.takeIf { it.isNotEmpty() }?.toInt() ?: 0
        )
    }


    override fun mapToEntity(domainModel: Settings): PingResponse {
        return PingResponse()
    }
}