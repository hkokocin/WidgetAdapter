package de.welt.widgetadapter

import android.support.v7.util.DiffUtil

class SimpleDiffCallback(
        val newItems: List<Any>,
        val oldItems: List<Any>,
        val areItemsTheSame: (Any, Any) -> Boolean,
        val areContentsTheSame: (Any, Any) -> Boolean
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        areItemsTheSame(oldItems[oldItemPosition], newItems[newItemPosition])

    override fun getOldListSize() = oldItems.size

    override fun getNewListSize() = newItems.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        areContentsTheSame(oldItems[oldItemPosition], newItems[newItemPosition])
}