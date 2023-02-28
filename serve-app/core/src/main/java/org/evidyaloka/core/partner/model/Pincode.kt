package org.evidyaloka.core.partner.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Pincode(
    val district: String? = null,
    val id: Int = 0,
    val pincode: String? = null,
    val taluk: String? = null
) : Parcelable