package org.evidyaloka.student.ui.student

import org.evidyaloka.core.student.model.Student

interface OnStudentSelectedListener {
    fun onStudentSelected(student: Student)
}