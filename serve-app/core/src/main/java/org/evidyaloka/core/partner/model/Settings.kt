package org.evidyaloka.core.partner.model

import org.evidyaloka.core.model.ContentSettings

data class Settings(
        var userId: Int = 0,
        var userStatus: String = "",
        var sessionId: String = "",
        var sessionExpiryTime: String = "",
        var partnerStatus: String = "",
        var partnerId: Int = 0,
        var gradeTo: Int = 0,
        var gradeFrom: Int = 0,
        var partnerContentSettings: ContentSettings? = null
)