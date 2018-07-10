package de.welt.widgetadapter

import android.support.v7.util.DiffUtil

interface Diffable {
    fun areItemsTheSame(other: Any): Boolean = other == this
    fun areContentsTheSame(other: Any): Boolean = other == this
}

class SimpleDiffCallback(
        val newItems: List<Any>,
        val oldItems: List<Any>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldItems[oldItemPosition]
        val newItem = newItems[newItemPosition]

        return if (oldItem is Diffable)
            oldItem.areItemsTheSame(newItem)
        else
            oldItem == newItem
    }

    override fun getOldListSize() = oldItems.size

    override fun getNewListSize() = newItems.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldItems[oldItemPosition]
        val newItem = newItems[newItemPosition]

        return if (oldItem is Diffable)
            oldItem.areContentsTheSame(newItem)
        else
            oldItem == newItem
    }
}
