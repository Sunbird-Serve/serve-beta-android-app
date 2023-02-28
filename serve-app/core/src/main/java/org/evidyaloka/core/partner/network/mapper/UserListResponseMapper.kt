package org.evidyaloka.core.partner.network.mapper

import org.evidyaloka.core.partner.model.User
import org.evidyaloka.core.partner.network.entity.DigitalSchoolEntity
import org.evidyaloka.core.partner.network.entity.UserListResponse
import org.evidyaloka.core.partner.network.entity.dsm.SchoolsAssigned
import org.evidyaloka.core.helper.EntityMapper
import javax.inject.Inject

/**
 * @author Madhankumar
 * created on 29-12-2020
 *
 */
class UserListResponseMapper @Inject
constructor() : EntityMapper<UserListResponse, List<User>> {
    override fun mapFromEntity(entity: UserListResponse): List<User> {
        val iterator = listOf(entity.data.userList).listIterator()
        var users = ArrayList<User>()
        for (item in iterator) {
            item.iterator().forEach {
                var digitalSchools = ArrayList<DigitalSchoolEntity>()
                digitalSchools.clear()
                it.digitalSchools?.forEach {
                    digitalSchools.add(DigitalSchoolEntity(it.id, it.digitalSchoolName))
                }

                var schools = ArrayList<SchoolsAssigned>()

                it.schoolsAssigned?.forEach {
                    schools.add(
                            SchoolsAssigned(
                                    purpose = it.purpose,
                                    description = it.description,
                                    id = it.id,
                                    logoUrl = it.logoUrl,
                                    name = it.name
                            )
                    )
                }

                users.add(User(
                        email = it.email?:"",
                        id = it.id?:0,
                        phone = it.phone?:"",
                        fname = it.fname?:"",
                        lname = it.lname?:"",
                        roles = it.roles,
                        profilePicId = it.profilePicId?:0,
                        profileImageUrl = it.profileImage?:"",
                        address = it.address?:"",
                        schoolCount = it.schoolCount?:0,
                        userStatus = it.userStatus?:"",
                        digitalSchools = it.digitalSchools?: listOf(),
                        schoolsAssigned = it.schoolsAssigned?: listOf(),
                        taluk = it.taluk ?: "",
                        district = it.district ?: "",
                        state = it.state ?: "",
                        pincode = it.pincode ?: ""
                )
                )
            }
        }
        return users
    }

    override fun mapToEntity(domainModel: List<User>): UserListResponse {
        return UserListResponse()
    }
}