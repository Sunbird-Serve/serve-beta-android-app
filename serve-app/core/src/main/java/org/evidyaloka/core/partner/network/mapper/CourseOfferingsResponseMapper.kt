package org.evidyaloka.core.partner.network.mapper

import org.evidyaloka.core.partner.model.CourseOffering
import org.evidyaloka.core.partner.network.entity.course.offerings.CourseOfferingResponse
import org.evidyaloka.core.helper.EntityMapper
import javax.inject.Inject

class CourseOfferingsResponseMapper @Inject
constructor() : EntityMapper<CourseOfferingResponse, List<CourseOffering>> {
    override fun mapFromEntity(entity: CourseOfferingResponse): List<CourseOffering> {
        var courseOfferingList = ArrayList<CourseOffering>()
        entity.data.offerings.forEach {
            courseOfferingList.add(CourseOffering(
                    id = it.id?:0,
                    grade = it.grade?:"",
                    courseName = it.courseName?:"",
                    AcademicYear = it.AcademicYear?:"",
                    CourseProvider = it.CourseProvider?:"",
                    StartDate = it.StartDate?:"",
                    EndDate = it.EndDate?:""))
        }
        return courseOfferingList
    }

    override fun mapToEntity(domainModel: List<CourseOffering>): CourseOfferingResponse {
        return CourseOfferingResponse()
    }

}