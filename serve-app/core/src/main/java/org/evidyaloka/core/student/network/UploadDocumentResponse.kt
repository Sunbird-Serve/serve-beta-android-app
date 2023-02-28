package org.evidyaloka.core.student.network


import com.google.gson.annotations.SerializedName

data class UploadDocumentResponse(
    @SerializedName("data")
    val data:UploadDocumentEntity = UploadDocumentEntity(),
    @SerializedName("status")
    val status: String? = "",
    @SerializedName("statusCode")
    val statusCode: Int? = 0
) {

}