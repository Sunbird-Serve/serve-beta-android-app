package org.evidyaloka.core.student.network.mapper

import org.evidyaloka.core.helper.EntityMapper
import org.evidyaloka.core.model.Subject
import org.evidyaloka.core.student.network.entity.SubjectResponse
import javax.inject.Inject

/**
 * @author Madhankumar
 * created on 14-04-2021
 *
 */
class SubjectMapper @Inject
constructor() : EntityMapper<SubjectResponse, List<Subject>> {
    override fun mapFromEntity(entity: SubjectResponse): List<Subject> {
        return mutableListOf<Subject>().apply {
            entity.data?.subjects?.forEach {
                add(
                    Subject(
                    courseId = it.courseId?:0,
                    offeringId = it.offeringId?:0,
                    subjectName = it.subjectName?:"",
                    totalTopics = it.totalTopics?:0,
                    totalTopicsViewed = it.totalTopicsViewed?:0
                )
                )
            }
        }
    }

    override fun mapToEntity(domainModel: List<Subject>): SubjectResponse {
//        TODO("Not yet implemented")
        return SubjectResponse()
    }
}