package de.welt.widgetadapter

import android.support.v7.widget.RecyclerView
import android.view.View
import de.welt.widgetadapter.Widget

class WidgetViewHolder<T>(val widget: Widget<T>, view: View) : RecyclerView.ViewHolder(view)
