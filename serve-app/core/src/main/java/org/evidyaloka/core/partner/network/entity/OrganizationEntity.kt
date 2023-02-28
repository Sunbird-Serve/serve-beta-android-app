package org.evidyaloka.core.partner.network.entity

import com.google.gson.annotations.SerializedName

/**
 * @author Madhankumar
 * created on 04-01-2021
 *
 */
data class OrganizationEntity(
    @SerializedName("address")
    val address: String? = "",
    @SerializedName("email")
    val email: String? = "",
    @SerializedName("id")
    val id: String? = "",
    @SerializedName("isDocumentUploaded")
    val isDocumentUploaded: Boolean? = false,
    @SerializedName("isVerified")
    val isVerified: Boolean? = false,
    @SerializedName("name")
    val name: String? = "",
    @SerializedName("partnerName")
    val partnerName: String? = "",
    @SerializedName("phone")
    val phone: String? = "",
    @SerializedName("partnerStatus")
    val partnerStatus: String? = "",
    @SerializedName("logo")
    val logo: String? = ""

)