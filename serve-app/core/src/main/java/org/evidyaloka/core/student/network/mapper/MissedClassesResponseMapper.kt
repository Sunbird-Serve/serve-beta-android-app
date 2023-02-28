package org.evidyaloka.core.student.network.mapper

import org.evidyaloka.core.helper.EntityMapper
import org.evidyaloka.core.student.model.Generic
import org.evidyaloka.core.student.model.MissedClasses
import org.evidyaloka.core.student.network.entity.GenericResponse
import org.evidyaloka.core.student.network.entity.MissedClassesResponse
import javax.inject.Inject

/**
 * @author Madhankumar
 * created on 29-12-2020
 *
 */
class MissedClassesResponseMapper @Inject
constructor() : EntityMapper<MissedClassesResponse, MissedClasses> {
    override fun mapFromEntity(entity: MissedClassesResponse): MissedClasses {
        return entity.data?.let {
            MissedClasses(
                count = it.count,
                firstMissedDate = it.firstMissedDate?:""
            )
        } ?: MissedClasses()
    }

    override fun mapToEntity(domainModel: MissedClasses): MissedClassesResponse {
        return MissedClassesResponse()
    }
}