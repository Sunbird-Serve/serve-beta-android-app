package org.evidyaloka.core.partner.network.entity


import com.google.gson.annotations.SerializedName

class UserResponse(
    @SerializedName("data")
    val data: Data = Data(),
    @SerializedName("status")
    val status: String = "",
    @SerializedName("statusCode")
    val statusCode: Int = 0
) {
    data class Data(
        @SerializedName("organization")
        val organizationEntity: OrganizationEntity = OrganizationEntity(),
        @SerializedName("user")
        val userEntity: UserEntity = UserEntity()
    )
}