package org.evidyaloka.core.helper

/**
 * @author Madhankumar
 * created on 29-12-2020
 *
 */
interface EntityMapper <Entity, DomainModel>{

    fun mapFromEntity(entity: Entity): DomainModel

    fun mapToEntity(domainModel: DomainModel): Entity
}