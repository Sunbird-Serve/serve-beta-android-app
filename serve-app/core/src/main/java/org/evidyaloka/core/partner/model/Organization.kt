package org.evidyaloka.core.partner.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @author Madhankumar
 * created on 29-12-2020
 *
 */
@Parcelize
data class Organization(
        var address: String = "",
        var email: String = "",
        var id: String = "",
        var isDocumentUploaded: Boolean = false,
        var isVerified: Boolean = false,
        var partnerStatus: String = "",
        var name: String = "",
        var phone: String = "",
        var partnerName: String = "",
        var logo: String = ""
): Parcelable