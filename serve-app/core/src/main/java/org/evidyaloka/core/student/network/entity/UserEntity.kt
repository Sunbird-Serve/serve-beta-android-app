package org.evidyaloka.core.student.network.entity

import com.google.gson.annotations.SerializedName

data class UserEntity(
    @SerializedName("email")
    val email: String? = "",
    @SerializedName("userId", alternate = ["id"])
    val id: Int? = 0,
    @SerializedName(value = "name", alternate = ["fullName"])
    val name: String? = "",
    @SerializedName("phone", alternate = ["mobile"])
    val phone: String? = "",
    @SerializedName("roles")
    val roles: List<String> = listOf(),
    @SerializedName("userStatus", alternate = ["status", "useruserStatus"])
    val status: String? = "",
    @SerializedName("sessionId")
    val sessionId: String? = "",
    @SerializedName("sessionExpiryTime")
    val sessionExpiryTime: String? = "",
    @SerializedName(value = "profileImage", alternate = ["pictureUrl"])
    val profileImage: String? = "",
    @SerializedName("createdDate")
    val createdDate: Long? = 0,
    @SerializedName("kycStatus")
    val kycStatus: Boolean? = false
)