package org.evidyaloka.core.partner.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.evidyaloka.core.partner.network.entity.dsm.DigitalSchools
import org.evidyaloka.core.partner.network.entity.dsm.SchoolsAssigned

/**
 * @author Madhankumar
 * created on 26-12-2020
 *
 */
@Parcelize
data class User(
        var email: String = "",
        var id: Int = 0,
        var fname: String = "",
        var lname: String = "",
        var phone: String = "",
        var roles: List<String> = arrayListOf<String>(),
        val isResetPwdRequired: Boolean = false,
        var userStatus: String = "",
        var sessionId: String = "",
        val sessionExpiryTime: String = "",
        var partnerStatus: String = "",
        var partnerId: Int = 0,
        var profileImageUrl: String = "",
        var profileImageShortUrl: String = "",
        var profileImageFullUrl: String = "",
        var organization: Organization = Organization(),
        val profilePicId: Long = 0,
        val address: String = "",
        val schoolCount: Int = 0,
        val digitalSchools: List<DigitalSchools> = ArrayList(),
        val schoolsAssigned: List<SchoolsAssigned> = ArrayList(),
        /*
        for Dsm Details
         */
        val taluk: String = "",
        val district: String = "",
        val state: String = "",
        val pincode: String = ""
): Parcelable