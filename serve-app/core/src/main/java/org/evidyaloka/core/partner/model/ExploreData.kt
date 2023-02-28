package org.evidyaloka.core.partner.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.evidyaloka.core.model.ExploreSchool

@Parcelize
data class ExploreData(
    var relationShip: String? = "parent",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var pincode: Int = 0,
    var grade: Int = 0,
    var courseProviderId: Int = 0,
    var school: ExploreSchool? = null,
    var user: User? = null
) : Parcelable
