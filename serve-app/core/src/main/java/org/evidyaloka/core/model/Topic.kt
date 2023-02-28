package org.evidyaloka.core.model


/**
 * @author Madhankumar
 * created on 14-04-2021
 *
 */
data class Topic(
    val id: Int = 0,
    val name: String = "",
    val subtopics: List<SubTopic> = listOf()
)
