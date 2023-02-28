package org.evidyaloka.core.partner.network.mapper

import android.R
import org.evidyaloka.core.helper.EntityMapper
import org.evidyaloka.core.model.Course
import org.evidyaloka.core.model.ExploreSchool
import org.evidyaloka.core.partner.network.entity.ExploreDigitalSchoolResponse
import javax.inject.Inject


class ExploreDigitalSchoolResponseMapper @Inject
constructor() : EntityMapper<ExploreDigitalSchoolResponse, ExploreSchool> {
    override fun mapFromEntity(entity: ExploreDigitalSchoolResponse): ExploreSchool {
        entity.data?.schools?.takeIf { it.size > 0 }?.get(0)?.let{
            return ExploreSchool(
                bannerUrl = it.bannerUrl?:"",
                description = it.description?:"",
                dsmId = it.dsmId?:0,
                dsmName = it.dsmName?:"",
                enrolledStudentCount = it.enrolledStudentCount?:0,
                id = it.id?:0,
                medium = it.medium?:"",
                name = it.name?:"",
                partnerId = it.partnerId?:0,
                partnerName = it.partnerName?:"",
                schoolLogoUrl = it.schoolLogoUrl?:"",
                statementOfPurpose = it.statementOfPurpose?:"",
                status = it.statementOfPurpose?:"",
                courses = getCourse(it.courses)
            )
        }
        return ExploreSchool()
    }

    private fun getCourse(courses: List<ExploreDigitalSchoolResponse.Data.School.Course?>?): List<Course> {
        var coursesList = ArrayList<Course>()
        courses?.forEach {
            coursesList.add(
                    Course(
                        id = it?.courseId?:0,
                        offeringId = it?.offeringId?:0,
                        name = it?.name?:""
                    )
            )
        }
        return coursesList
    }


    override fun mapToEntity(domainModel: ExploreSchool): ExploreDigitalSchoolResponse {
        return ExploreDigitalSchoolResponse()
    }

}