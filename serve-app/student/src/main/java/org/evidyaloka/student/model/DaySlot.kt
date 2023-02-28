package org.evidyaloka.student.model

data class DaySlot(
    var id : Int = 0,
    var title: String = "",
    var status: Boolean = false,
    var selectable : Boolean = true
)