package org.evidyaloka.core.partner.network.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.evidyaloka.core.partner.network.entity.dsm.DigitalSchools
import org.evidyaloka.core.partner.network.entity.dsm.SchoolsAssigned

/**
 * @author Madhankumar
 * created on 04-01-2021
 *
 */
data class UserEntity(
        @SerializedName("email")
        val email: String? = "",
        @SerializedName("userId", alternate = ["id"])
        val id: Int? = 0,
        @SerializedName(value = "fname", alternate = ["firstName"])
        val fname: String? = "",
        @SerializedName(value = "lname", alternate = ["lastName"])
        val lname: String? = "",
        @SerializedName("phone")
        val phone: String? = "",
        @SerializedName("roles")
        val roles: List<String> = listOf(),
        @SerializedName("isResetPwdRequired")
        val isResetPwdRequired: Int? = 0,
        @SerializedName("userStatus", alternate = ["status", "useruserStatus"])
        val userStatus: String? = "",
        @SerializedName("sessionId")
        val sessionId: String? = "",
        @SerializedName("sessionExpiryTime")
        val sessionExpiryTime: String? = "",
        @SerializedName("partnerStatus")
        val partnerStatus: String? = "",
        @SerializedName("partnerId")
        val partnerId: Int? = 0,
        @SerializedName(value = "profileImage", alternate = ["pictureUrl"])
        val profileImage: String? = "",
        @SerializedName(value = "profileImageShortUrl")
        val profileImageShortUrl: String? = null,
        @SerializedName(value = "profileImageFullUrl")
        val profileImageFullUrl: String? = null,
        @Expose(serialize = false)
        @SerializedName("profilePicId")
        val profilePicId: Long? = 0,
        @Expose
        @SerializedName("address")
        val address: String? = "",
        @Expose(serialize = false)
        @SerializedName("schoolCount")
        val schoolCount: Int? = 0,
        @Expose(serialize = false)
        @SerializedName("digitalSchools")
        val digitalSchools: List<DigitalSchools>? = ArrayList(),
        @SerializedName("schoolsAssigned")
        @Expose
        val schoolsAssigned: List<SchoolsAssigned>? = ArrayList(),

        /*
        For DSM User Details
         */

        @SerializedName("taluk")
        @Expose
        val taluk: String? = null,

        @Expose
        @SerializedName("district")
        val district: String? = null,

        @Expose
        @SerializedName("state")
        val state: String? = null,

        @Expose
        @SerializedName("pincode")
        val pincode: String? = null

)