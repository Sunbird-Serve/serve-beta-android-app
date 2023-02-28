package org.evidyaloka.core.partner.network.entity

import com.google.gson.annotations.SerializedName

data class PincodeEntity(
    @SerializedName("district")
    val district: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("pincode")
    val pincode: String?,
    @SerializedName("taluk")
    val taluk: String?
)