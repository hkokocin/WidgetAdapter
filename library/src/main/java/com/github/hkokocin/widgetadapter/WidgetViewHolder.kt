package com.github.hkokocin.widgetadapter

import android.support.v7.widget.RecyclerView
import android.view.View
import com.github.hkokocin.widgetadapter.Widget

class WidgetViewHolder<T>(val widget: Widget<T>, view: View) : RecyclerView.ViewHolder(view)
