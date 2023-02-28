package org.evidyaloka.core.student.network.mapper

import org.evidyaloka.core.helper.EntityMapper
import org.evidyaloka.core.model.SubTopic
import org.evidyaloka.core.student.network.entity.SubtopicsResponse
import javax.inject.Inject

/**
 * @author Madhankumar
 * created on 22-03-2021
 *
 */
class SubtopicsResponseMapper @Inject
constructor() : EntityMapper<SubtopicsResponse, List<SubTopic>> {
    override fun mapFromEntity(entity: SubtopicsResponse): List<SubTopic> {
        val iterator = listOf(entity.data?.subtopics).listIterator()
        var subtopics = ArrayList<SubTopic>()
        for(item in iterator){
            item.iterator().forEach {
                subtopics.add(SubTopic(it.id?:0, it.subtopicName?:""))
            }
        }
        return subtopics
    }

    override fun mapToEntity(domainModel: List<SubTopic>): SubtopicsResponse {
        return SubtopicsResponse()
    }
}