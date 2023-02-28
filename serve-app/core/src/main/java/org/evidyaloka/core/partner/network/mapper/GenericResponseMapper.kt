package org.evidyaloka.core.partner.network.mapper

import org.evidyaloka.core.partner.model.Generic
import org.evidyaloka.core.partner.network.entity.GenericResponse
import org.evidyaloka.core.helper.EntityMapper
import javax.inject.Inject

/**
 * @author Madhankumar
 * created on 29-12-2020
 *
 */
class GenericResponseMapper @Inject
constructor(): EntityMapper<GenericResponse,Generic> {
    override fun mapFromEntity(entity: GenericResponse): Generic {
        return entity.data?.let{
            Generic(
                    id = it.id?:"",
                    message = it.message?:""
            )
        }?: Generic()
    }

    override fun mapToEntity(domainModel: Generic): GenericResponse {
        return GenericResponse()
    }
}