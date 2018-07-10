package de.welt.widgetadapter

import android.content.res.Resources
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlin.reflect.KClass

class WidgetViewHolder<T>(val widget: Widget<T>, view: View) : RecyclerView.ViewHolder(view)

abstract class Widget<T>(@field:LayoutRes private val layoutId: Int) {

    abstract fun setData(data: T)

    protected lateinit var view: View

    fun createView(inflater: LayoutInflater, parent: ViewGroup?): View {
        view = inflater.inflate(layoutId, parent, false)
        onViewCreated(view)
        return view
    }

    open fun onViewRecycled() {}
    open fun onViewBound() {}

    open protected fun onViewCreated(view: View) {}

    protected fun <T : View> viewId(@IdRes id: Int): Lazy<T> = lazy { view.findViewById<T>(id) }

    inline fun <reified T : Any> resourceId(resourcesId: Int) = lazy {
        getResource(resourcesId, T::class)
    }

    fun colorResource(resourcesId: Int, theme: Resources.Theme? = null) = lazy {
        view.resources.getColorInt(resourcesId, theme)
    }

    fun <T : Any> getResource(resourcesId: Int, type: KClass<T>) = getResource(view.resources, resourcesId, type)

    fun dimensionInPixels(resourcesId: Int) = lazy {
        view.resources.getDimensionPixelSize(resourcesId)
    }
}
