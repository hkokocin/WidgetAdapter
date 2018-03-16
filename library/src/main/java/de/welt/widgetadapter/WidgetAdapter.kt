package de.welt.widgetadapter

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import java.util.*

open class WidgetAdapter(
        val layoutInflater: LayoutInflater
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var supportsCollapsableGroups: Boolean = false
    var widgetProviders = LinkedHashMap<Class<out Any>, () -> Widget<*>>()

    var areItemsTheSame: (Any, Any) -> Boolean = { old, new -> old == new }
    var areContentsTheSame: (Any, Any) -> Boolean = { old, new -> old == new }

    private var allItems = listOf<Any>()
    private var visibleItems = listOf<Any>()

    inline fun <reified T : Any> addWidget(noinline widgetProvider: () -> Widget<T>) {
        widgetProviders[T::class.java] = widgetProvider
    }

    fun setItems(items: List<Any>) {
        doSetItems(items)
        notifyDataSetChanged()
    }

    fun getItems() = allItems

    fun updateItems(items: List<Any>) {
        val oldItems = this.visibleItems
        doSetItems(items)
        DiffUtil.calculateDiff(SimpleDiffCallback(this.visibleItems, oldItems, areItemsTheSame, areContentsTheSame))
                .dispatchUpdatesTo(this)
    }

    fun swapItems(fromPosition: Int, toPosition: Int) {
        if (supportsCollapsableGroups) throw UnsupportedOperationException("swapping items on adapters that support collapsable groups is not supported yet. Please raise an issue if you need this feature: https://github.com/WeltN24/WidgetAdapter/issues")

        Collections.swap(visibleItems, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun getItemCount() = visibleItems.size

    override fun getItemViewType(position: Int) = widgetProviders.keys.indexOf(visibleItems[position].javaClass)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        bindViewHolder(holder, visibleItems[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        val provider = widgetProviders.values.elementAt(viewType)
        val widget = provider()
        return WidgetViewHolder(widget, widget.createView(layoutInflater, parent))
    }

    private fun doSetItems(items: List<Any>) {
        allItems = items
        visibleItems = if (supportsCollapsableGroups) items.visibleItems else items
    }

    private val List<Any>.visibleItems: List<Any>
        get() {
            if (widgetProviders.isEmpty()) throw IllegalStateException("You have to add widget providers before you set items.")

            return if (supportsCollapsableGroups)
                applyCollapsedState()
            else
                filter { widgetProviders.contains(it.javaClass) }
        }

    private fun List<Any>.applyCollapsedState(): List<Any> {
        val collapsedStates = this
                .filterIsInstance<CollapsableGroupHeaderWidgetData>()
                .map { it.collapsableGroupId to it.isCollapsed }
                .toMap()

        return this.filter { widgetProviders.contains(it.javaClass) && it.isNotCollapsed(collapsedStates) }
    }

    private fun Any.isNotCollapsed(collapsedStates: Map<String, Boolean>): Boolean {
        val id = (this as? CollapsableWidgetData)?.collapsableGroupId ?: return false
        return collapsedStates[id] ?: false
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> bindViewHolder(holder: RecyclerView.ViewHolder, item: T) {
        val viewHolder = holder as WidgetViewHolder<T>
        viewHolder.widget.setData(item as T)
    }
}