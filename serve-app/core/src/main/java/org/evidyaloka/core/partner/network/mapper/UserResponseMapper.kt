package org.evidyaloka.core.partner.network.mapper

import org.evidyaloka.core.partner.model.Organization
import org.evidyaloka.core.partner.model.User
import org.evidyaloka.core.partner.network.entity.OrganizationEntity
import org.evidyaloka.core.partner.network.entity.UserResponse
import org.evidyaloka.core.helper.EntityMapper
import javax.inject.Inject

/**
 * @author Madhankumar
 * created on 29-12-2020
 *
 */
class UserResponseMapper @Inject
constructor() : EntityMapper<UserResponse, User> {
    override fun mapFromEntity(entity: UserResponse): User {
        return User(
            email = entity.data.userEntity.email?:"",
            id = entity.data.userEntity.id?:0,
            phone = entity.data.userEntity.phone?:"",
            fname = entity.data.userEntity.fname?:"",
            lname = entity.data.userEntity.lname?:"",
            sessionId = entity.data.userEntity.sessionId?:"",
            sessionExpiryTime = entity.data.userEntity.sessionExpiryTime?:"",
            roles = entity.data.userEntity.roles,
            userStatus = entity.data.userEntity.userStatus?:"",
            isResetPwdRequired = entity.data.userEntity.isResetPwdRequired == 1,
            partnerId = entity.data.userEntity.partnerId?:0,
            partnerStatus = entity.data.userEntity.partnerStatus?:"",
            profileImageUrl = entity.data.userEntity.profileImage?:"",
            profileImageShortUrl = entity.data.userEntity.profileImageShortUrl ?: "",
            profileImageFullUrl = entity.data.userEntity.profileImageFullUrl ?: "",
            organization = mapOrganization(entity.data.organizationEntity),
            taluk = entity.data.userEntity.taluk ?: "",
            district = entity.data.userEntity.district ?: "",
            state = entity.data.userEntity.state ?: "",
            pincode = entity.data.userEntity.pincode ?: "",
            digitalSchools = entity.data.userEntity.digitalSchools?:listOf(),
            schoolsAssigned = entity.data.userEntity.schoolsAssigned?: listOf()
        )
    }

    private fun mapOrganization(entity: OrganizationEntity): Organization {

        return Organization(
            address = entity.address?:"",
            email = entity.email?:"",
            id = entity.id?:"",
            isDocumentUploaded = entity.isDocumentUploaded?:false,
            isVerified = entity.isVerified?:false,
            name = entity.name?:"",
            phone = entity.phone?:"",
            partnerName = entity.partnerName?:"",
            partnerStatus = entity.partnerStatus?:"",
            logo = entity.logo?:""

        )

    }

    override fun mapToEntity(domainModel: User): UserResponse {
        return UserResponse()
    }
}