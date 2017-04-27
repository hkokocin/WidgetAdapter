package de.welt.widgetadapter

import java.util.*
import kotlin.reflect.KClass

data class Handler<in T>(val tag: Any, val handler: (T) -> Unit)

class EventDispatcher {

    private val callbacksByType = HashMap<KClass<*>, MutableList<Handler<*>>>()

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> handlersOf(type: KClass<T>): MutableList<Handler<T>> {
        if (!callbacksByType.containsKey(type))
            callbacksByType.put(type, mutableListOf<Handler<*>>())

        return callbacksByType[type]!! as MutableList<Handler<T>>
    }

    inline fun <reified T : Any> subscribe(tag: Any, noinline callback: (event: T) -> Unit) {
        handlersOf(T::class).add(Handler<T>(tag, callback))
    }

    inline fun <reified T : Any> subscribe(noinline callback: (event: T) -> Unit) {
        handlersOf(T::class).add(Handler<T>(this, callback))
    }

    fun unsubscribeAll() = callbacksByType.clear()
    fun unsubscribeUntagged() = callbacksByType.values.forEach { it.removeAll { it.tag == this } }
    fun unsubscribeAllOf(tag: Any) = callbacksByType.values.forEach { it.removeAll { it.tag == tag } }
    inline fun <reified T : Any> unsubscribe(tag: Any) = handlersOf(T::class).removeAll { it.tag == tag }

    @Suppress("UNCHECKED_CAST")
    fun dispatch(event: Any) = callbacksByType
            .filterKeys { it.java.isAssignableFrom(event.javaClass) }
            .values.flatMap { it }
            .distinct()
            .map { it as Handler<Any> }
            .forEach { it.handler(event) }

    infix fun delegate(eventDispatcher: EventDispatcher) = eventDispatcher.subscribe<Any>(this) { dispatch(it) }
}