package org.evidyaloka.core.student.network.mapper

import org.evidyaloka.core.helper.EntityMapper
import org.evidyaloka.core.student.model.Doubt
import org.evidyaloka.core.student.model.DoubtList
import org.evidyaloka.core.student.network.entity.doubt.DoubtEntity
import org.evidyaloka.core.student.network.entity.doubt.DoubtListResponse
import javax.inject.Inject

class DoubtListResponseMapper @Inject
constructor() : EntityMapper<DoubtListResponse, DoubtList> {
    override fun mapFromEntity(entity: DoubtListResponse): DoubtList {

        return DoubtList(
            totalCount = entity.data?.totalCount ?: 0,
            doubts = mapSessionList(entity.data.doubts)
        )
    }

    fun mapSessionList(doubts: List<DoubtEntity>): MutableList<Doubt> {
        return mutableListOf<Doubt>().apply {
            doubts.forEach {
                add(
                    Doubt(
                        id = it.id ?: 0,
                        offeringId = it.offeringId ?: 0,
                        subjectName = it.subTopicName ?: "",
                        topicId = it.topicId ?: 0,
                        subTopicId = it.subTopicId ?: 0,
                        topicName = it.topicName ?: "",
                        subTopicName = it.subTopicName ?: "",
                        createdDate = it.createdDate ?: "",
                        status = it.status ?: "",
                        url = it.url ?: "",
                        contentType = it.contentType ?: "",
                        teacherName = it.teacherName ?: "",
                        recordType = it.recordType ?: "",
                        resourceType = it.resourceType ?: "",
                        text = it.text ?: "",
                        contentHost = it.contentHost ?: "",
                        thumbnailUrl = it.thumbnailUrl ?: ""
                    )
                )
            }
        }
    }

    override fun mapToEntity(domainModel: DoubtList): DoubtListResponse {
        return DoubtListResponse()
    }
}