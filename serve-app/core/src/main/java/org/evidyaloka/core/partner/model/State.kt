package org.evidyaloka.core.partner.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class State(
    val id: Int=0,
    val code: String?,
    val name: String?,
    var pincodes: List<Pincode> = listOf()
) : Parcelable