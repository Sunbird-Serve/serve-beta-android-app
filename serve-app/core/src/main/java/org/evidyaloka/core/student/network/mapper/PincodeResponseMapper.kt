package org.evidyaloka.core.student.network.mapper

import org.evidyaloka.core.helper.EntityMapper
import org.evidyaloka.core.student.model.Pincode
import org.evidyaloka.core.student.network.entity.GetPincodeResponse
import javax.inject.Inject

class PincodeResponseMapper @Inject constructor() :
    EntityMapper<GetPincodeResponse, List<Pincode>> {

    override fun mapFromEntity(entity: GetPincodeResponse): List<Pincode> {
        return mutableListOf<Pincode>().apply {
            entity.data.pincodes.forEach {
                add(
                    Pincode(
                        it.district, it.id, it.pincode, it.taluk
                    )
                )
            }
        }
    }

    override fun mapToEntity(domainModel: List<Pincode>): GetPincodeResponse {
        return GetPincodeResponse()
    }

}