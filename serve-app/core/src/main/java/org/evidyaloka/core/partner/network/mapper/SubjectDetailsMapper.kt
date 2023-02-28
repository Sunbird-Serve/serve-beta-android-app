package org.evidyaloka.core.partner.network.mapper

import org.evidyaloka.core.helper.EntityMapper
import org.evidyaloka.core.model.SubTopic
import org.evidyaloka.core.model.Topic
import org.evidyaloka.core.partner.network.entity.SubjectDetailsResponse

import javax.inject.Inject

/**
 * @author Madhankumar
 * created on 14-04-2021
 *
 */
class SubjectDetailsMapper @Inject
constructor() : EntityMapper<SubjectDetailsResponse, List<Topic>> {
    override fun mapFromEntity(entity: SubjectDetailsResponse): List<Topic> {
        return mutableListOf<Topic>().apply {
            entity.data.topics.forEach {
                add(
                    Topic(
                        id = it.id?:0,
                        name = it.name?:"",
                        subtopics = mapSubTopic(it.subtopics)
                    )
                )
            }
        }

    }

    override fun mapToEntity(domainModel: List<Topic>): SubjectDetailsResponse {
        TODO("Not yet implemented")
    }

    fun mapSubTopic(subtopics: List<SubjectDetailsResponse.Data.Topic.Subtopic>): List<SubTopic> {
        return mutableListOf<SubTopic>().apply {
            subtopics.forEach {
                add(
                    SubTopic(
                        id = it.id?:0,
                        subtopicName = it.name?:"",
                        hasViewed = it.hasViewed == 1
                    )
                )
            }
        }
    }

}