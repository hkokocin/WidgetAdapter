package de.welt.widgetadapter

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlin.reflect.KClass

abstract class SimpleWidget<T>(private val layout: Int) : Widget<T>() {

    var viewCreated = false
        private set

    private lateinit var contentView: View
    protected lateinit var resources: Resources

    protected val context: Context
        get() = contentView.context

    inline fun <reified T : Any> resourceId(resourcesId: Int) = lazy {
        getResource(resourcesId, T::class)
    }

    fun colorResource(resourcesId: Int, theme: Resources.Theme? = null) = lazy {
        resources.getColorInt(resourcesId, theme)
    }

    fun <T : Any> getResource(resourcesId: Int, type: KClass<T>) = getResource(resources, resourcesId, type)

    fun dimensionInPixels(resourcesId: Int) = lazy {
        resources.getDimensionPixelSize(resourcesId)
    }

    override fun createView(inflater: LayoutInflater, container: ViewGroup?): View {
        contentView = inflater.inflate(layout, container, false)
        resources = contentView.resources
        viewCreated = true
        onViewCreated(contentView)
        return contentView
    }
}