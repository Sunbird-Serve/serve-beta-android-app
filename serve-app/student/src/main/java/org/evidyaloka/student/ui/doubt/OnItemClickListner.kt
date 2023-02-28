package org.evidyaloka.student.ui.doubt

import org.evidyaloka.core.Constants.StudentConst
import org.evidyaloka.core.student.model.Doubt

interface OnItemClickListner {
    fun OnItemClick(doubt: Doubt, type : StudentConst.DoubtViewType? = null)
}