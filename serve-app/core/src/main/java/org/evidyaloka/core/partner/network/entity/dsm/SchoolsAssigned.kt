package org.evidyaloka.core.partner.network.entity.dsm

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class
SchoolsAssigned(
        @SerializedName("purpose")
        @Expose
        val purpose: String? = "",

        @SerializedName("description")
        @Expose
        val description: String? = "",

        @SerializedName("logoUrl")
        @Expose
        val logoUrl: String? = "",

        @SerializedName("id")
        @Expose
        val id: Int? = 0,

        @SerializedName("name")
        @Expose
        val name: String? = ""
) : Parcelable