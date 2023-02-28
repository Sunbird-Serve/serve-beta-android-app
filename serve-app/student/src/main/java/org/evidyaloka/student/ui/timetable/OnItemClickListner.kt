package org.evidyaloka.student.ui.timetable

import org.evidyaloka.core.model.Session

/**
 * @author Madhankumar
 * created on 25-03-2021
 *
 */
interface OnItemClickListner {
    fun OnItemClick(session: Session)
}