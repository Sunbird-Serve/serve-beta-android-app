package org.evidyaloka.core.student.model

data class Timetable(
    val startTime: String? = "",
    val subjectName: String? = "",
    val topicName: String? = "",
    val teacherName: String? = "",
    val classType: String? = "",
    val status: String? = ""
)