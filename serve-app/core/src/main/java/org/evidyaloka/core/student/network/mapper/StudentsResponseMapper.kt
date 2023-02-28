package org.evidyaloka.core.student.network.mapper

import org.evidyaloka.core.helper.EntityMapper
import org.evidyaloka.core.student.model.School
import org.evidyaloka.core.student.model.Student
import org.evidyaloka.core.student.network.entity.DigitalSchoolEntity
import org.evidyaloka.core.student.network.entity.StudentsResponse
import javax.inject.Inject

class StudentsResponseMapper @Inject
constructor() : EntityMapper<StudentsResponse, List<Student>> {
    override fun mapFromEntity(entity: StudentsResponse): List<Student> {
        var list: ArrayList<Student> = ArrayList()

        entity?.data?.let { responseData ->
            for (item in responseData?.students) {
                item?.let { student ->
                    list.add(
                        Student(
                            id = student.id ?: 0,
                            status = student.status ?: "",
                            onboardingStatus = student.onboardingStatus ?: "",
                            classStatus = student.classStatus?:1,
                            name = student.name ?: "",
                            gender = student.gender ?: "",
                            grade = student.grade ?: "",
                            profileUrl = student.profileUrl ?: "",
                            schools = mapToSchool(student.schoolAssigned)
                        )
                    )
                }
            }
        }
        return list
    }

    private fun mapToSchool(entity: DigitalSchoolEntity): School {
        return School(
            id = entity?.id ?: 0,
            logoUrl = entity?.logoUrl ?: "",
            name = entity?.name ?: "",
            boardName = entity?.boardName?: "",
            boardCode = entity?.boardCode?: "",
            status = entity?.status ?: ""
        )
    }

    override fun mapToEntity(domainModel: List<Student>): StudentsResponse {
        return StudentsResponse()
    }
}