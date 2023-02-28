package org.evidyaloka.core.partner.network.entity

import com.google.gson.annotations.SerializedName

data class StateEntity(
    @SerializedName("code")
    val code: String?,
    @SerializedName("id",alternate = ["stateId"])
    val id: Int = 0,
    @SerializedName("name")
    val name: String?,
    @SerializedName("pincodes")
    val pincodeEntities: List<PincodeEntity> = listOf()
)