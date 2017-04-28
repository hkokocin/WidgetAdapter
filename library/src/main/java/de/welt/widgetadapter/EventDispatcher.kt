package de.welt.widgetadapter

import java.util.*
import kotlin.reflect.KClass

data class Handler<in T>(val tag: Any, val handler: (T) -> Unit)

class EventDispatcher {

    private val callbacksByType = HashMap<KClass<*>, MutableList<Handler<*>>>()

    inline fun <reified T : Any> subscribe(tag: Any, noinline callback: (event: T) -> Unit) {
        subscribe(T::class, tag, callback)
    }

    inline fun <reified T : Any> subscribe(noinline callback: (event: T) -> Unit) {
        subscribe(T::class, this, callback)
    }

    fun <T: Any> subscribe(type: KClass<T>, tag: Any, callback: (event: T) -> Unit) {
        handlersOf(type).add(Handler<T>(tag, callback))
    }

    fun unsubscribeAll() = callbacksByType.clear()

    fun unsubscribeUntagged() = callbacksByType.values.forEach { it.removeAll { it.tag == this } }
    fun unsubscribeAllOf(tag: Any) = callbacksByType.values.forEach { it.removeAll { it.tag == tag } }

    inline fun <reified T : Any> unsubscribe(tag: Any) = unsubscribe(T::class, tag)
    fun <T : Any> unsubscribe(type: KClass<T>, tag: Any) = handlersOf(type).removeAll { it.tag == tag }

    @Suppress("UNCHECKED_CAST")
    fun dispatch(event: Any) = callbacksByType
            .filterKeys { it.java.isAssignableFrom(event.javaClass) }
            .values.flatMap { it }
            .distinct()
            .map { it as Handler<Any> }
            .forEach { it.handler(event) }

    infix fun delegate(eventDispatcher: EventDispatcher) = eventDispatcher.subscribe<Any>(this) { dispatch(it) }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> handlersOf(type: KClass<T>): MutableList<Handler<T>> {
        if (!callbacksByType.containsKey(type))
            callbacksByType.put(type, mutableListOf<Handler<*>>())

        return callbacksByType[type]!! as MutableList<Handler<T>>
    }
}