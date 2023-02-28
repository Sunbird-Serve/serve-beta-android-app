package org.evidyaloka.core.student.network.mapper

import org.evidyaloka.core.helper.EntityMapper
import org.evidyaloka.core.student.model.Generic
import org.evidyaloka.core.student.model.SubTopicSession
import org.evidyaloka.core.student.network.entity.GenericResponse
import org.evidyaloka.core.student.network.entity.SubTopicSessionResponse
import javax.inject.Inject

/**
 * @author Madhankumar
 * created on 29-12-2020
 *
 */
class SubTopicSessionResponseMapper @Inject
constructor() : EntityMapper<SubTopicSessionResponse, SubTopicSession> {
    override fun mapFromEntity(entity: SubTopicSessionResponse): SubTopicSession {
        return entity.data?.let {
            SubTopicSession(
                offeringId = it.offeringId,
                sessionId = it.sessionId,
                subtopicId = it.subtopicId,
                timetableId = it.timetableId,
                topicId = it.topicId
            )
        } ?: SubTopicSession()
    }

    override fun mapToEntity(domainModel: SubTopicSession): SubTopicSessionResponse {
        return SubTopicSessionResponse()
    }
}