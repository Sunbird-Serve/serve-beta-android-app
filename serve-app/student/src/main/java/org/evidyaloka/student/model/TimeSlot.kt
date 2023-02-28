package org.evidyaloka.student.model

data class TimeSlot(
    var title: String? = "",
    var timing: String? = "",
    var key: String? = "",
    var icon: Int? = null,
    var background : Int? = null,
    var textColor : Int? = null,
    var status : Boolean = false,
    var selectable : Boolean = true
)
