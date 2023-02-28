package org.evidyaloka.core.partner.network.mapper

import org.evidyaloka.core.partner.network.entity.DigitalSchoolEntity
import org.evidyaloka.core.partner.network.entity.SchoolsResponse
import org.evidyaloka.core.helper.EntityMapper
import org.evidyaloka.core.model.CourseProvider
import org.evidyaloka.core.partner.model.*
import org.evidyaloka.core.partner.network.entity.PincodeEntity
import org.evidyaloka.core.partner.network.entity.StateEntity
import javax.inject.Inject

class SchoolResponseMapper @Inject
constructor() : EntityMapper<SchoolsResponse, DigitalSchoolList> {
    override fun mapFromEntity(entity: SchoolsResponse): DigitalSchoolList {
        val iterator = listOf(entity.data.schools).listIterator()
        var schools = ArrayList<DigitalSchool>()
        for (item in iterator) {
            item.iterator().forEach {

                schools.add(DigitalSchool(
                        bannerId = it.bannerId,
                        bannerUrl = it.bannerUrl,
                        centerId = it.centerId,
                        courseCount = it.courseCount?:0,
                        createdOn = it.createdOn?:"",
                        description = it.description?:"",
                        dsmFirstName = it.dsmFirstName,
                        dsmLastName = it.dsmLastName,
                        dsmId = it.dsmId,
                        dsmUsername = it.dsmUsername,
                        id = it.id?:0,
                        logoId = it.logoId?:0,
                        logoUrl = it.logoUrl?:"",
                        name = it.name?:"",
                        purpose = it.purpose?:"",
                        status = it.status?:"",
                        studentCount = it.studentCount?:0,
                        students = students(it.students),
                        selectedState = mapState(it.selectedStates) as MutableList<State>,
                        courseProviderList = getCourseProviders(it)))
            }
        }
        return DigitalSchoolList(schools)
    }

    private fun getCourseProviders(entity: DigitalSchoolEntity): List<CourseProvider> {
        var courseProviderList = ArrayList<CourseProvider>()
        entity.courseProviderList?.forEach {
            courseProviderList.add(
                    CourseProvider(
                            code = it.code?:"",
                            id = it.id?:0,
                            name = it.name?:"",
                            type = it.type)
            )
        }
        return courseProviderList
    }

    private fun students(students: List<DigitalSchool.Student>): List<DigitalSchool.Student> {
        var list = ArrayList<DigitalSchool.Student>()
        for (student in students.listIterator()) {
            students.iterator().forEach {
                list.add(DigitalSchool.Student(
                        id = it.id,
                        name = it.name,
                        profileUrl = it.profileUrl
                ))
            }
        }
        return list
    }

    private fun mapState(selectedStates: List<StateEntity>): List<State>{
        var list = ArrayList<State>()
        selectedStates?.forEach {
            list.add(
                State(
                    id = it.id,
                    code = it.code,
                    name = it.name,
                    pincodes = mapPincode(it.pincodeEntities)
                )
            )
        }
        return list
    }

    private fun mapPincode(pincodeEntities: List<PincodeEntity>): List<Pincode>{
        var list = ArrayList<Pincode>()
        pincodeEntities?.forEach {
            list.add(
                Pincode(
                    id = it.id?:0,
                    taluk = it.taluk,
                    pincode = it.pincode,
                    district = it.district
                )
            )
        }
        return list
    }

    override fun mapToEntity(domainModel: DigitalSchoolList): SchoolsResponse {
        return SchoolsResponse()
    }

}