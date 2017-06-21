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

    val events = EventDispatcher()

    var parts = LinkedHashMap<KClass<out Any>, WidgetViewHolderProvider<*, *>>()
        set(value) {
            field = value
            field.forEach { events.delegate(it.value.events) }
        }

    var areItemsTheSame: SimpleDiffCallback.(Any, Any) -> Boolean = { old, new -> old == new }

    private var items = listOf<Any>()

    inline fun <reified T : Any> addWidget(noinline widgetProvider: () -> Widget<T>) {
        val part = WidgetViewHolderProvider(layoutInflater, widgetProvider)
        events.delegate(part.events)
        part.events.delegate(events)
        parts.put(T::class, part)
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

    override fun getItemViewType(position: Int): Int {
        val part = getPartAt(position)
        return parts.values.indexOf(part)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val part = getPartAt(position)
        part.onBindViewHolder(holder, items[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        val part = parts.values.elementAt(viewType)
        return part.onCreateViewHolder(parent)
    }

    private fun getPartAt(position: Int): WidgetViewHolderProvider<*, *> {
        val item = items[position]
        val part = parts[item.javaClass.kotlin]

        part ?: throw IllegalStateException("No AdapterPart found for item type ${items[position].javaClass.name}")

        return part
    }
}