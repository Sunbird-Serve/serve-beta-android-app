package org.evidyaloka.core.partner.network.mapper

import org.evidyaloka.core.partner.model.Student
import org.evidyaloka.core.partner.model.StudentList
import org.evidyaloka.core.partner.network.entity.student.EnrolledStudentsResponse
import org.evidyaloka.core.helper.EntityMapper
import javax.inject.Inject

class EnrolledStudentsMapper @Inject
constructor() : EntityMapper<EnrolledStudentsResponse, StudentList> {
    override fun mapFromEntity(entity: EnrolledStudentsResponse): StudentList {
        val iterator = listOf(entity.data.students).listIterator()
        var students = ArrayList<Student>()
        for (item in iterator) {
            item.iterator().forEach {
                students.add(Student(
                       profileUrl = it.profileUrl?:"",
                        id = it.id?:0,
                        studentName = it.name?:"",
                        grade = it.grade?:""
                ))
            }
        }
        return StudentList(students.toList(),entity.data.count)
    }

    override fun mapToEntity(domainModel: StudentList): EnrolledStudentsResponse {
        TODO("Not yet implemented")
    }

}