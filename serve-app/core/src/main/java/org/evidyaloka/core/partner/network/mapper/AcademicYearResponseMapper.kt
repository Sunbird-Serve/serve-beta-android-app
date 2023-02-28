package org.evidyaloka.core.partner.network.mapper

import org.evidyaloka.core.partner.model.AcademicYear
import org.evidyaloka.core.partner.network.entity.course.academic.AcademicYearResponse
import org.evidyaloka.core.helper.EntityMapper
import javax.inject.Inject

class AcademicYearResponseMapper @Inject
constructor() : EntityMapper<AcademicYearResponse, List<AcademicYear>> {
    override fun mapFromEntity(entity: AcademicYearResponse): List<AcademicYear> {
        var academicYearList = ArrayList<AcademicYear>()
        entity.data.academicYears.forEach {
            academicYearList.add(AcademicYear(academicYear = it.academicYear?:"",
                    id = it.id?:0))
        }
        return academicYearList
    }

    override fun mapToEntity(domainModel: List<AcademicYear>): AcademicYearResponse {
        return AcademicYearResponse()
    }

}