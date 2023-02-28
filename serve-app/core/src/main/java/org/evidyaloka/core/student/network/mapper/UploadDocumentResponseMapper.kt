package org.evidyaloka.core.student.network.mapper

import org.evidyaloka.core.helper.EntityMapper
import org.evidyaloka.core.student.model.UploadDocument
import org.evidyaloka.core.student.network.UploadDocumentResponse
import javax.inject.Inject

/**
 * @author Madhankumar
 * created on 29-12-2020
 *
 */
class UploadDocumentResponseMapper @Inject
constructor(): EntityMapper<UploadDocumentResponse, UploadDocument> {
    override fun mapFromEntity(entity: UploadDocumentResponse): UploadDocument {
        return entity.data.let{
            UploadDocument(
                    id = it.id?:0,
                    name = it.name?:"",
                    message = it.message?:""
            )
        }
    }

    override fun mapToEntity(domainModel: UploadDocument): UploadDocumentResponse {
        return UploadDocumentResponse()
    }
}