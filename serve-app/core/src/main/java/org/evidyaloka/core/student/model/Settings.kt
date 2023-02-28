package org.evidyaloka.core.student.model

import org.evidyaloka.core.model.ContentSettings

data class Settings(
    var userId: Int = 0,
    var sessionId: String = "",
    var sessionExpiryTime: String = "",
    var userStatus: String = "",
    var otpExpityTime: Long = 0,
    var studyStudyTimeConfiguration: List<StudyTimeConfiguration> = listOf(),
    var parentContentSettings: ContentSettings? = null,
    var partnerContentSettings: ContentSettings? = null,
    var gradeTo: Int = 0,
    var gradeFrom: Int = 0

)