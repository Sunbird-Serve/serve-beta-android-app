package org.evidyaloka.core.student.network.mapper

import org.evidyaloka.core.helper.EntityMapper
import org.evidyaloka.core.student.model.EditSchedule
import org.evidyaloka.core.student.model.Generic
import org.evidyaloka.core.student.network.entity.EditScheduleResponse
import org.evidyaloka.core.student.network.entity.GenericResponse
import javax.inject.Inject

class EditScheduleResponseMapper @Inject
constructor() : EntityMapper<EditScheduleResponse, EditSchedule> {
    override fun mapFromEntity(entity: EditScheduleResponse): EditSchedule {
        return entity.data?.let {
            EditSchedule(
                id = it.id ?: 0,
                message = it.message ?: ""
            )
        } ?: EditSchedule()
    }

    override fun mapToEntity(domainModel: EditSchedule): EditScheduleResponse {
        return EditScheduleResponse()
    }
}