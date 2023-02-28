package org.evidyaloka.core.partner.network.mapper

import org.evidyaloka.core.partner.network.entity.DigitalSchoolEntity
import org.evidyaloka.core.partner.network.entity.dsm.SchoolsAssigned
import org.evidyaloka.core.helper.EntityMapper
import org.evidyaloka.core.partner.model.State
import org.evidyaloka.core.partner.network.entity.StateListResponse
import javax.inject.Inject

/**
 * @author Madhankumar
 * created on 29-12-2020
 *
 */
class StateListResponseMapper @Inject
constructor() : EntityMapper<StateListResponse, List<State>> {
    override fun mapFromEntity(entity: StateListResponse): List<State> {
        var states: MutableList<State> = mutableListOf()
        entity.data?.states?.forEach {
            states.add(State(it.id,it.code,it.name))
        }
        return states
    }

    override fun mapToEntity(domainModel: List<State>): StateListResponse {
        return StateListResponse()
    }
}