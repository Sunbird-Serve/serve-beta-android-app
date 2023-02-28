package org.evidyaloka.student.ui.myschools

import org.evidyaloka.core.model.Course

interface OnCourseClickListener {
    fun OnItemClick(course: Course)
}