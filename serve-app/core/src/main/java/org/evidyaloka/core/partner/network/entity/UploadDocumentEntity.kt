package org.evidyaloka.core.partner.network.entity

import com.google.gson.annotations.SerializedName

/**
 * @author Madhankumar
 * created on 07-01-2021
 *
 */
data class UploadDocumentEntity(
    @SerializedName("id")
    val id: Int? = 0,
    @SerializedName("message")
    val message: String? = "",
    @SerializedName("name")
    val name: String? = ""
)