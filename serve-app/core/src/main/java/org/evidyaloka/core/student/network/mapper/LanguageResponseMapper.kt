package org.evidyaloka.core.student.network.mapper

import org.evidyaloka.core.helper.EntityMapper
import org.evidyaloka.core.student.model.Language
import org.evidyaloka.core.student.network.LanguagesResponse
import javax.inject.Inject

/**
 * @author Madhankumar
 * created on 29-12-2020
 *
 */
class LanguageResponseMapper @Inject
constructor(): EntityMapper<LanguagesResponse,List<Language>> {
    override fun mapFromEntity(entity: LanguagesResponse): List<Language> {
        entity.data.let{
            var list: ArrayList<Language> = ArrayList()
            for(item in entity.data.languages) {
                list.add(
                    Language(
                        code = item.code?:"",
                        id = item.id?:0,
                        name = item.name?:""
                    )
                )
            }
            return list
        }
    }

    override fun mapToEntity(domainModel: List<Language>): LanguagesResponse {
        TODO("Not yet implemented")
    }


}