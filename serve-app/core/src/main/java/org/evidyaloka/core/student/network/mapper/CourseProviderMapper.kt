package org.evidyaloka.core.student.network.mapper

import org.evidyaloka.core.helper.EntityMapper
import org.evidyaloka.core.model.CourseProvider
import org.evidyaloka.core.student.network.entity.CourseProviderResponse
import javax.inject.Inject

/**
 * @author Madhankumar
 * created on 14-04-2021
 *
 */
class CourseProviderMapper @Inject
constructor() : EntityMapper<CourseProviderResponse, List<CourseProvider>> {
    override fun mapFromEntity(entity: CourseProviderResponse): List<CourseProvider> {
        return mutableListOf<CourseProvider>().apply {
            entity.data?.courseProviders?.forEach {
                add(
                    CourseProvider(
                        id = it.id,
                        code = it.code,
                        name = it.name,
                        type = it.type
                    )
                )
            }
        }
    }

    override fun mapToEntity(domainModel: List<CourseProvider>): CourseProviderResponse {
        return CourseProviderResponse()
    }
}