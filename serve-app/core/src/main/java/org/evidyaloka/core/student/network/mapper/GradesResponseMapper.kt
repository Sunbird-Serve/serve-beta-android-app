package org.evidyaloka.core.student.network.mapper

import org.evidyaloka.core.helper.EntityMapper
import org.evidyaloka.core.partner.model.Grade
import org.evidyaloka.core.student.model.Generic
import org.evidyaloka.core.student.network.entity.GenericResponse
import org.evidyaloka.core.student.network.entity.GradesResponse
import javax.inject.Inject

/**
 * @author Madhankumar
 * created on 29-12-2020
 *
 */
class GradesResponseMapper @Inject
constructor() : EntityMapper<GradesResponse, Grade> {
    override fun mapFromEntity(entity: GradesResponse): Grade {
        return entity.data?.let {
            Grade(
                grades = it.grades
            )
        } ?: Grade()
    }

    override fun mapToEntity(domainModel: Grade): GradesResponse {
        return GradesResponse()
    }
}