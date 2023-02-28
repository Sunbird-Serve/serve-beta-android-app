package org.evidyaloka.core.partner.network.entity


import com.google.gson.annotations.SerializedName

class LoginResponse(
    @SerializedName("data")
    val data: UserEntity = UserEntity(),
    @SerializedName("status")
    val status: String? = "",
    @SerializedName("statusCode")
    val statusCode: Int? = 0
)