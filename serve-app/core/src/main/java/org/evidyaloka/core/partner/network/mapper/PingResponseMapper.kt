package org.evidyaloka.core.partner.network.mapper

import org.evidyaloka.core.partner.model.Settings
import org.evidyaloka.core.partner.network.entity.ping.PingResponse
import org.evidyaloka.core.helper.EntityMapper
import org.evidyaloka.core.model.ContentSettings
import javax.inject.Inject

class PingResponseMapper @Inject
constructor() : EntityMapper<PingResponse, Settings> {
    override fun mapFromEntity(entity: PingResponse): Settings {
        return Settings(
                userId = entity.data.userId?:0,
                sessionId = entity.data.sessionId?:"",
                sessionExpiryTime = entity.data.sessionExpiryTime.toString(),
                userStatus = entity.data.useruserStatus?:"",
                partnerId = entity.data.partnerId?:0,
                partnerStatus = entity.data.partnerStatus?:"",
                gradeTo = entity.data.settings.supportedGrades.to?:0,
                gradeFrom = entity.data.settings.supportedGrades.from?:0,
            partnerContentSettings = mapContentSettings(entity.data.settings.allowedChapterForNewUser.partner)
        )
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