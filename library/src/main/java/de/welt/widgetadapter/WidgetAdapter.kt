package de.welt.widgetadapter

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import java.util.*
import kotlin.reflect.KClass

open class WidgetAdapter(
        val layoutInflater: LayoutInflater
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var widgetProviders = LinkedHashMap<Class<out Any>, () -> Widget<*>>()

    var areItemsTheSame: SimpleDiffCallback.(Any, Any) -> Boolean = { old, new -> old == new }

    private var items = listOf<Any>()

    inline fun <reified T : Any> addWidget(noinline widgetProvider: () -> Widget<T>) {
        widgetProviders.put(T::class.java, widgetProvider)
    }

    fun setItems(items: List<Any>) {
        this.items = items
        notifyDataSetChanged()
    }

    fun getItems() = items

    fun updateItems(items: List<Any>) {
        val oldItems = this.items
        this.items = items
        DiffUtil.calculateDiff(SimpleDiffCallback(items, oldItems, areItemsTheSame))
                .dispatchUpdatesTo(this)
    }

    fun swapItems(fromPosition: Int, toPosition: Int) {
        Collections.swap(getItems(), fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int) = widgetProviders.keys.indexOf(items[position].javaClass)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        bindViewHolder(holder, items[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        val provider = widgetProviders.values.elementAt(viewType)
        val widget = provider()
        return WidgetViewHolder(widget, widget.createView(layoutInflater, parent))
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> bindViewHolder(holder: RecyclerView.ViewHolder, item: T) {
        val viewHolder = holder as WidgetViewHolder<T>
        viewHolder.widget.setData(item as T)
    }
}