package org.evidyaloka.core.student.network.mapper

import org.evidyaloka.core.helper.EntityMapper
import org.evidyaloka.core.model.Course
import org.evidyaloka.core.student.model.UpdateOffering
import org.evidyaloka.core.student.network.entity.OfferingResponse
import org.evidyaloka.core.student.network.entity.UpdateOfferingResponse
import javax.inject.Inject

/**
 * @author Madhankumar
 * created on 17-03-2021
 *
 */
class UpdateOfferingMapper @Inject
constructor() : EntityMapper<UpdateOfferingResponse, UpdateOffering> {
    override fun mapFromEntity(entity: UpdateOfferingResponse): UpdateOffering {
        return UpdateOffering(entity.data.isUpdateScheduleRequired,entity.data.message?:"")
    }

    override fun mapToEntity(domainModel: UpdateOffering): UpdateOfferingResponse {
        return UpdateOfferingResponse()
    }
}