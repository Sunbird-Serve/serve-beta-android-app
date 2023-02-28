package org.evidyaloka.core.student.model

/**
 * @author GaurawY
 * created on 16-03-2021
 *
 */
data class User(
    var email: String = "",
    var id: Int = 0,
    var name: String = "",
    var phone: String = "",
    var roles: List<String> = arrayListOf<String>(),
    var status: String = "",
    var sessionId: String = "",
    val sessionExpiryTime: String = "",
    var profileImageUrl: String = "",
    var createdDate: Long? = 0,
    var kycStatus : Boolean = false,
    var userType : String = "guardian"

)