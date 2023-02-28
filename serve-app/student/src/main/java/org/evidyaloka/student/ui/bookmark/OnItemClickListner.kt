package org.evidyaloka.student.ui.bookmark

import org.evidyaloka.core.student.database.entity.BookmarkEntity

interface OnItemClickListner {
    fun OnItemClick(bookmark: BookmarkEntity)
}