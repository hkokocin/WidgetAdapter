package com.github.hkokocin.widgetadapter

import android.support.v7.util.DiffUtil

class SimpleDiffCallback(
        val newItems: List<Any>,
        val oldItems: List<Any>,
        val areItemsTheSame: SimpleDiffCallback.(Any, Any) -> Boolean
) : DiffUtil.Callback() {

    override fun areItemsTheSame(
            oldItemPosition: Int,
            newItemPosition: Int): Boolean {

        val newItem = newItems[newItemPosition]
        val oldItem = oldItems[oldItemPosition]

        return areItemsTheSame(oldItem, newItem)
    }

    override fun getOldListSize() = oldItems.size

    override fun getNewListSize() = newItems.size

    override fun areContentsTheSame(
            oldItemPosition: Int,
            newItemPosition: Int): Boolean {

        return newItems[newItemPosition] == oldItems[oldItemPosition]
    }
}