package org.evidyaloka.core.partner.network.entity


import com.google.gson.annotations.SerializedName

class UserListResponse(
    @SerializedName("data")
    val data: Data = Data(),
    @SerializedName("status")
    val status: String = "",
    @SerializedName("statusCode")
    val statusCode: Int = 0
) {
    data class Data(
        @SerializedName("users")
        val userList: List<UserEntity> = listOf<UserEntity>()
    )
}