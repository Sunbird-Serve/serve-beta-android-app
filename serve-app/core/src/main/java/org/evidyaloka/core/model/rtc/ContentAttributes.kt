package org.evidyaloka.core.model.rtc

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ContentAttributes(
    val id: Int = 0,
    val subtopicId: Int = 0, //Todo add subtopicId value from
    val title: String? = "",
    val subtitle: String? = "",
    val description: String? = "",
    val author: String? = "",
    val contentHost: String? = "", // [s3,youtube,vimeo]
    val contentType: String? = "", // [ms-doc,pdf,url,ms-xls,ms-ppt]
    var viewStatus: Int? = null, // [1=read, 0= not read]
    val url: String? = "",
    val duration: Int? = 0,
    val thumbnailUrl: String? = "",
    val progress: Int? = 0,
    val type: String? = "",
    val subTopicTitle: String? = "",
    var localUrl: String? = null,
    var downloadStatus: Int? = null
) : Parcelable
