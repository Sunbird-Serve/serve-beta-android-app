package org.evidyaloka.core.student.network.mapper

import org.evidyaloka.core.helper.EntityMapper
import org.evidyaloka.core.student.model.StudyPreference
import org.evidyaloka.core.student.model.StudyTimeConfiguration
import org.evidyaloka.core.student.network.entity.StudyPreferenceResponse
import javax.inject.Inject

class StudyPreferenceResponseMapper @Inject
constructor() : EntityMapper<StudyPreferenceResponse, StudyPreference> {
    override fun mapFromEntity(entity: StudyPreferenceResponse): StudyPreference {
        return StudyPreference(entity.data?.days, mapSlotList(entity.data?.slots))
    }

    fun mapSlotList(slots: List<org.evidyaloka.core.student.network.entity.StudyTimeConfiguration>): MutableList<StudyTimeConfiguration> {
        return mutableListOf<StudyTimeConfiguration>().apply {
            slots.forEach {
                add(
                    StudyTimeConfiguration(
                        displayName = it.displayName ?: "",
                        endTimeMin = it.endTimeMin ?: 0,
                        startTimeMin = it.startTimeMin ?: 0,
                        startTime = it.startTime ?: 0,
                        endTime = it.endTime ?: 0,
                        key = it.key ?: ""

                    )
                )
            }
        }
    }

    override fun mapToEntity(domainModel: StudyPreference): StudyPreferenceResponse {
        TODO("Not yet implemented")
    }
}



