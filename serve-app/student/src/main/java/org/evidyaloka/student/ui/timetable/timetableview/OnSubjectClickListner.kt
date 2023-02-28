package org.evidyaloka.student.ui.timetable.timetableview

import org.evidyaloka.core.model.Subject

/**
 * @author Madhankumar
 * created on 25-03-2021
 *
 */
interface OnSubjectClickListner {
    fun OnItemClick(subject: Subject)
}