package de.welt.widgetadapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import de.welt.widgetadapter.EventDispatcher

class WidgetViewHolderProvider<T, W : Widget<T>>(
        private val inflater: LayoutInflater,
        private val widgetProvider: () -> W
) {
    val events = EventDispatcher()

    @Suppress("UNCHECKED_CAST")
    fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any) {
        val viewHolder = holder as WidgetViewHolder<T>
        viewHolder.widget.setData(item as T)
    }

    fun onCreateViewHolder(parent: ViewGroup?): RecyclerView.ViewHolder {
        val widget = widgetProvider()
        events.delegate(widget.events)
        widget.events.delegate(events)
        return WidgetViewHolder(widget, widget.createView(inflater, parent))
    }
}