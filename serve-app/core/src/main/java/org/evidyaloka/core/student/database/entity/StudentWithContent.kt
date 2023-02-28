package org.evidyaloka.core.student.database.entity

import androidx.room.Embedded
import androidx.room.Relation

data class StudentWithContent(
    @Embedded val student: StudentEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "studentId"
    ) val contentEntity: List<CourseContentEntity>
)
