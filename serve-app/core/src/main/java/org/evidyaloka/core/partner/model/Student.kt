package org.evidyaloka.core.partner.model

import org.evidyaloka.core.Constants.PartnerConst


data class Student(
        val dob: String = "",
        val gender: String = "",
        val grade: String = "",
        val parentName: String = "",
        val id: Int = 0,
        val mobile: String = "",
        val studentName: String = "",
        val physicalSchoolName: String = "",
        val profileUrl: String = "",
        val relationshipType: String = "",
        val hasTakenGuardianConsent: Boolean = false,
        val digitalSchoolId: Int = 0,
        val partnerId:Int = 0,
        val profilePicId: Int = 0,
        val offeringsOpted: List<OfferingsOpted>? = null,
        val mediumOfStudy: MediumOfStudy? = null,
        var count:Int? = null,
        val onboardingStatus: String? = null,
        val status: PartnerConst.StudentStatus? = null
)