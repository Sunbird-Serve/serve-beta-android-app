package org.evidyaloka.core.partner.network.mapper

import org.evidyaloka.core.Constants.PartnerConst
import org.evidyaloka.core.partner.model.Student
import org.evidyaloka.core.helper.EntityMapper
import org.evidyaloka.core.partner.model.MediumOfStudy
import org.evidyaloka.core.partner.model.OfferingsOpted
import org.evidyaloka.core.partner.network.entity.student.StudentResponse
import javax.inject.Inject

class StudentsResponseMapper @Inject
constructor() : EntityMapper<StudentResponse, Student> {
    override fun mapFromEntity(entity: StudentResponse): Student {
        entity.data.studentEntity?.let {
            return Student(
                dob = it.dob?:"",
                gender = it.gender?:"",
                grade = it.grade?:"",
                parentName = it.guardianName?:"",
                id = it.id?:0,
                mobile = it.mobile?:"",
                profileUrl = it.profileUrl ?: "",
                studentName = it.name?:"",
                physicalSchoolName = it.physicalSchoolName?:"",
                relationshipType = it.relationshipType?:"",
                offeringsOpted = mapOfferings(it.offeringsOpted),
                mediumOfStudy = mapMediumOfStudy(it.mediumOfStudy),
                onboardingStatus = it.onboardingStatus,
                status = it.status?.let { it1 -> PartnerConst.StudentStatus.formatOf(it1) }
            )
        }
    }

    private fun mapOfferings(offeringsOpted: List<org.evidyaloka.core.partner.network.entity.student.OfferingsOpted>?):List<OfferingsOpted> {
        val list = ArrayList<OfferingsOpted>()
        offeringsOpted?.forEach {
            list.add(
                OfferingsOpted(
                    grade = it.grade?:"",
                    id = it.id?:0,
                    subject = it.subject?:""
                )
            )
        }
        return list
    }

    private fun mapMediumOfStudy(mediumOfStudy: org.evidyaloka.core.partner.network.entity.student.MediumOfStudy):MediumOfStudy{
        return MediumOfStudy(mediumOfStudy.languageId?:0,mediumOfStudy.name?:"")
    }

    override fun mapToEntity(domainModel: Student): StudentResponse {
        TODO("Not yet implemented")
    }

}