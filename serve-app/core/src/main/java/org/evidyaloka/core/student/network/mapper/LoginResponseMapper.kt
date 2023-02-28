package org.evidyaloka.core.student.network.mapper

import org.evidyaloka.core.helper.EntityMapper
import org.evidyaloka.core.student.model.User
import org.evidyaloka.core.student.network.entity.LoginResponse
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
            id = entity.data.id?:0,
            phone = entity.data.phone?:"",
            name = entity.data.name?:"",
            sessionId = entity.data.sessionId?:"",
            sessionExpiryTime = entity.data.sessionExpiryTime?:"",
            roles = entity.data.roles,
            status = entity.data.status?:"",
            createdDate = entity.data.createdDate,
            kycStatus = entity.data.kycStatus?:false

        )
    }


    override fun mapToEntity(domainModel: User): LoginResponse {
        return LoginResponse()
    }
}