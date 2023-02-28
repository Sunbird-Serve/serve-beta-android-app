package org.evidyaloka.common.interfaces

interface IAdapter<E> {
    fun setItems(list: List<E>)
    fun getItems(): List<E>
}