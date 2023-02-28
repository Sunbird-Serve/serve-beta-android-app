package org.evidyaloka.core.partner.network.mapper

import org.evidyaloka.core.partner.model.Generic
import org.evidyaloka.core.partner.network.entity.GenericResponse
import org.evidyaloka.core.helper.EntityMapper
import org.evidyaloka.core.partner.model.CheckCourse
import org.evidyaloka.core.partner.network.entity.CheckCourseResponse
import javax.inject.Inject

/**
 * @author Madhankumar
 * created on 29-12-2020
 *
 */
class CheckCourseResponseMapper @Inject
constructor(): EntityMapper<CheckCourseResponse,CheckCourse> {
    override fun mapFromEntity(entity: CheckCourseResponse): CheckCourse {
        return CheckCourse(isSimilarOfferingExist = entity?.data?.isSimilarOfferingExist == 1)
    }

    override fun mapToEntity(domainModel: CheckCourse): CheckCourseResponse {
        return CheckCourseResponse()
    }
}