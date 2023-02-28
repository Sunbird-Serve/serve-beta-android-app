package org.evidyaloka.core.partner.model

import com.google.gson.annotations.SerializedName

data class Grade(
    @SerializedName("grades")
    var grades: List<Int> = ArrayList()
)
