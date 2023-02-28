package org.evidyaloka.core.partner.network.mapper

import org.evidyaloka.core.partner.model.User
import org.evidyaloka.core.partner.network.entity.LoginResponse
import org.evidyaloka.core.helper.EntityMapper
import javax.inject.Inject

/**
 * @author Madhankumar
 * created on 29-12-2020
 *
 */
class LoginResponseMapper @Inject
constructor() : EntityMapper<LoginResponse, User> {
    override fun mapFromEntity(entity: LoginResponse): User {
        return User(
                email = entity.data.email?:"",
                id = entity.data.id?:0,
                phone = entity.data.phone?:"",
                fname = entity.data.fname?:"",
                lname = entity.data.lname?:"",
                sessionId = entity.data.sessionId?:"",
                sessionExpiryTime = entity.data.sessionExpiryTime?:"",
                roles = entity.data.roles,
                userStatus = entity.data.userStatus?:"",
                isResetPwdRequired = entity.data.isResetPwdRequired == 1,
                partnerId = entity.data.partnerId?:0,
                partnerStatus = entity.data.partnerStatus?:""
        )
    }


    override fun mapToEntity(domainModel: User): LoginResponse {
        return LoginResponse()
    }
}