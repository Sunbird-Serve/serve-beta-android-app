package org.evidyaloka.core.partner.network.mapper

import org.evidyaloka.core.model.Course
import org.evidyaloka.core.partner.network.entity.course.course.CourseResponse
import org.evidyaloka.core.helper.EntityMapper
import javax.inject.Inject

class CourseResponseMapper @Inject
constructor() : EntityMapper<CourseResponse, List<Course>> {
    override fun mapFromEntity(entity: CourseResponse): List<Course> {
        var courseList = ArrayList<Course>()
        entity.data.courses.forEach {
            courseList.add(
                Course(
                    grade = it.grade?:"",
                    id = it.id?:0,
                    name = it.name?:""
            )
            )
        }
        return courseList
    }

    override fun mapToEntity(domainModel: List<Course>): CourseResponse {
        return CourseResponse()
    }

}