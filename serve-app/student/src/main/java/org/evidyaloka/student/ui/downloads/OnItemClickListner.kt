package org.evidyaloka.student.ui.downloads

import org.evidyaloka.core.student.database.entity.CourseContentEntity

interface OnItemClickListner {
    fun OnItemClick(content: CourseContentEntity)
}