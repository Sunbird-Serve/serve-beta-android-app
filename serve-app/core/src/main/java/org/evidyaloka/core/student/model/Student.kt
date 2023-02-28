package org.evidyaloka.core.student.model
import org.evidyaloka.core.partner.network.entity.student.MediumOfStudy
import org.evidyaloka.core.partner.network.entity.student.OfferingsOpted

data class Student(
        val dob: String = "",
        val gender: String = "",
        val grade: String = "",
        val parentName: String = "",
        val id: Int = 0,
        val mobile: String = "",
        val name: String = "",
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
        var onboardingStatus: String? = null,
        var classStatus: Int? = null,//('1', 'Active'), ('2', 'Inactive'), ('3', 'Promoted ,Course not opted'),('4', 'schedule not opted ')
        var status: String? = null,
        var schools: School? = null

)