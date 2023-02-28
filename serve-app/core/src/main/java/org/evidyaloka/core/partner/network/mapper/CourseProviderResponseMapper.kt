package org.evidyaloka.core.partner.network.mapper

import org.evidyaloka.core.model.CourseProvider
import org.evidyaloka.core.partner.network.entity.course.provider.CourseProviderResponse
import org.evidyaloka.core.helper.EntityMapper
import javax.inject.Inject

class CourseProviderResponseMapper @Inject
constructor() : EntityMapper<CourseProviderResponse, List<CourseProvider>> {
    override fun mapFromEntity(entity: CourseProviderResponse): List<CourseProvider> {
        var courseProviderList = ArrayList<CourseProvider>()
        entity.data.courseProviderList.forEach {
            courseProviderList.add(
                    CourseProvider(
                            code = it.code,
                            id = it.id?:0,
                            name = it.name?:"",
                            type = it.type?:"")
            )
        }
        return courseProviderList
    }

    override fun mapToEntity(domainModel: List<CourseProvider>): CourseProviderResponse {
        return CourseProviderResponse()
    }
}