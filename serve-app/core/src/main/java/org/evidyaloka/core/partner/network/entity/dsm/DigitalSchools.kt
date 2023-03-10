package org.evidyaloka.core.partner.network.entity.dsm

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DigitalSchools(
		@Expose
		@SerializedName("id")
		val id: Int? = 0,
		@Expose
		@SerializedName("digitalSchoolName")
		val digitalSchoolName: String? = ""
): Parcelable