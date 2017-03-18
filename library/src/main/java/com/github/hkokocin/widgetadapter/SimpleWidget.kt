package com.github.hkokocin.widgetadapter

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.hkokocin.androidkit.app.getResource
import com.github.hkokocin.androidkit.content.getColorInt
import com.github.hkokocin.widgetadapter.Widget
import kotlin.reflect.KClass

abstract class SimpleWidget<T>(private val layout: Int) : Widget<T> {

    var viewCreated = false
        private set

    private lateinit var contentView: View
    protected lateinit var resources: Resources

    protected val context: Context
        get() = contentView.context

    @Suppress("UNCHECKED_CAST")
    fun <T : View> viewId(resourcesId: Int) = lazy {
        contentView.findViewById(resourcesId) as T
    }

    @Suppress("UNCHECKED_CAST")
    fun colorResource(resourcesId: Int, theme: Resources.Theme? = null) = lazy {
        resources.getColorInt(resourcesId, theme)
    }

    fun <T : Any> resourceId(resourcesId: Int, type: KClass<T>) = lazy {
        getResource(resources, resourcesId, type)
    }

    @Suppress("UNCHECKED_CAST")
    fun dimensionInPixels(resourcesId: Int) = lazy {
        resources.getDimensionPixelSize(resourcesId)
    }

    open fun onViewCreated(view: View) {}

    override fun createView(inflater: LayoutInflater, container: ViewGroup?): View {
        contentView = inflater.inflate(layout, container, false)
        resources = contentView.resources
        viewCreated = true
        onViewCreated(contentView)
        return contentView
    }
}