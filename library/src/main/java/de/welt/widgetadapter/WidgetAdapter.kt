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
        DiffUtil.calculateDiff(SimpleDiffCallback(this.visibleItems, oldItems))
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val provider = widgetProviders.values.elementAt(viewType)
        val widget = provider()
        return WidgetViewHolder(widget, widget.createView(layoutInflater, parent))
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        val viewHolder = holder as WidgetViewHolder<*>
        viewHolder.widget.onViewRecycled()
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
        val collapsedGroups = this
                .filterIsInstance<CollapsableGroupHeaderWidgetData>()
                .filter { it.isCollapsed }
                .map { it.collapsableGroupId }

        return this.filter { widgetProviders.contains(it.javaClass) && it.isNotCollapsed(collapsedGroups) }
    }

    private fun Any.isNotCollapsed(collapsedGroups: List<String>): Boolean {
        val id = (this as? CollapsableWidgetData)?.collapsableGroupId ?: return true
        return !collapsedGroups.contains(id)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> bindViewHolder(holder: RecyclerView.ViewHolder, item: T) {
        (holder as WidgetViewHolder<T>).widget.apply {
            setData(item as T)
            onViewBound()
        }
    }
}
