package org.evidyaloka.core.student.model

import com.google.gson.annotations.SerializedName

class MissedClasses(
    val count: Int? = 0,
    val firstMissedDate: String? = ""
)