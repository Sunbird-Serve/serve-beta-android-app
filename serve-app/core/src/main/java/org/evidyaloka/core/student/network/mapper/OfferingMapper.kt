package org.evidyaloka.core.student.network.mapper

import org.evidyaloka.core.helper.EntityMapper
import org.evidyaloka.core.partner.model.User
import org.evidyaloka.core.partner.network.entity.UserListResponse
import org.evidyaloka.core.model.Course
import org.evidyaloka.core.student.network.entity.OfferingResponse
import javax.inject.Inject

/**
 * @author Madhankumar
 * created on 16-03-2021
 *
 */
class OfferingMapper @Inject
constructor() : EntityMapper<OfferingResponse, List<Course>> {
    override fun mapFromEntity(entity: OfferingResponse): List<Course> {
        return mutableListOf<Course>().apply {
            entity.data?.courses?.forEach {
                add(
                    Course(
                        id = it.id ?: 0,
                        name = it.name ?: "",
                        langaugeId = it.langaugeId?: 0,
                        languageName = it.languageName?: ""
                    )
                )
            }
        }
    }

    override fun mapToEntity(domainModel: List<Course>): OfferingResponse {
//        TODO("Not yet implemented")
        return OfferingResponse()
    }
}