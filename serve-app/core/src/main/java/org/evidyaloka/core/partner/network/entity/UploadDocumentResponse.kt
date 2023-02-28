package org.evidyaloka.core.partner.network.entity


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