package org.evidyaloka.core.student.network.mapper

import org.evidyaloka.core.helper.EntityMapper
import org.evidyaloka.core.model.Session
import org.evidyaloka.core.student.model.SessionList
import org.evidyaloka.core.student.network.entity.SessionResponse
import javax.inject.Inject

/**
 * @author Madhankumar
 * created on 22-03-2021
 *
 */
class SessionResponseMapper @Inject
constructor() : EntityMapper<SessionResponse, SessionList> {
    override fun mapFromEntity(entity: SessionResponse): SessionList {
        return SessionList(
            count = entity.data.count ?: 0,
            timeTableId = entity.data.timeTableId ?: 0,
            timetableStatus = entity.data.timetableStatus ?: "",
            session = mapSessionList(entity.data.sessions)
        )

    }

    override fun mapToEntity(domainModel: SessionList): SessionResponse {
        TODO("Not yet implemented")
    }

    fun mapSessionList(sessions: List<SessionResponse.Data.Session>): MutableList<Session> {
        return mutableListOf<Session>().apply {
            sessions.forEach {
                add(
                    Session(
                        id = it.id ?: 0,
                        calDate = it.calDate ?: "",
                        classType = it.classType ?: "",
                        dayOfWeek = it.dayOfWeek ?: 0,
                        endTime = it.endTime ?: "",
                        hasAttended = it.hasAttended == 1,
                        liveClassUrl = it.liveClassUrl ?: "",
                        offeringId = it.offeringId ?: 0,
                        startTime = it.startTime ?: "",
                        status = it.status ?: "",
                        subjectName = it.subjectName ?: "",
                        subtopicIdStr = it.subtopicIdStr ?: "",
                        teacherName = it.teacherName ?: "",
                        topicId = it.topicId ?: 0,
                        topicName = it.topicName ?: "",
                        videoLink = it.videoLink,
                        lngCode = it.lngCode ?: "",
                        lngName = it.lngName ?: ""
                    )
                )
            }
        }
    }

}