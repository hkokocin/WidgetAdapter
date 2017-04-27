package de.welt.widgetadapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.welt.widgetadapter.EventDispatcher

interface Widget<T> {

    val events: EventDispatcher

    fun setData(data: T)
    fun createView(inflater: LayoutInflater, container: ViewGroup?): View
}