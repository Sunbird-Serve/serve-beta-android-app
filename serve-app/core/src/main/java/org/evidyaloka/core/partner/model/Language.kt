package org.evidyaloka.core.partner.model

import com.google.gson.annotations.SerializedName

/**
 * @author Madhankumar
 * created on 01-02-2021
 *
 */
data class Language(
    @SerializedName("code")
    val code: String = "",
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("name")
    val name: String = ""
)