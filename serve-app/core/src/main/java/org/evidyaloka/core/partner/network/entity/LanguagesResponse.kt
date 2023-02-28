package org.evidyaloka.core.partner.network.entity


import com.google.gson.annotations.SerializedName

data class LanguagesResponse(
    @SerializedName("data")
    val `data`: Data = Data(),
    @SerializedName("status")
    val status: String? = "",
    @SerializedName("statusCode")
    val statusCode: Int? = 0
) {
    data class Data(
        @SerializedName("languages")
        val languages: List<Language> = listOf()
    ) {
        data class Language(
            @SerializedName("code")
            val code: String? = "",
            @SerializedName("id")
            val id: Int? = 0,
            @SerializedName("name")
            val name: String? = ""
        )
    }
}