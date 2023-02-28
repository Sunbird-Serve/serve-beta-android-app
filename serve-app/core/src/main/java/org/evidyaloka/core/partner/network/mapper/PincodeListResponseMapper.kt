package org.evidyaloka.core.partner.network.mapper

import org.evidyaloka.core.helper.EntityMapper
import org.evidyaloka.core.partner.model.Pincode
import org.evidyaloka.core.partner.network.entity.PinCodeResponse
import javax.inject.Inject

/**
 * @author Madhankumar
 * created on 29-12-2020
 *
 */
class PincodeListResponseMapper @Inject
constructor() : EntityMapper<PinCodeResponse, List<Pincode>> {
    override fun mapFromEntity(entity: PinCodeResponse): List<Pincode> {
        var pincodes: MutableList<Pincode> = mutableListOf()
        entity.data?.pincodeEntities?.forEach {
            pincodes.add(
                Pincode(
                    id = it.id?: 0,
                    district = it.district,
                    pincode = it.pincode,
                    taluk = it.taluk
                )
            )
        }
        return pincodes
    }

    override fun mapToEntity(domainModel: List<Pincode>): PinCodeResponse {
        return PinCodeResponse()
    }
}