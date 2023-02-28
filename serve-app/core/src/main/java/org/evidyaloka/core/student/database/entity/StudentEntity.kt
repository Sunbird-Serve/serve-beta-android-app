package org.evidyaloka.core.student.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class StudentEntity(
    @PrimaryKey
    val id: Int? = 0,
    val name: String? = "",
    val grade: String? = "",
    val schoolId: Int = 0,
    val logoUrl: String = "",
    val schoolName: String = ""
)
