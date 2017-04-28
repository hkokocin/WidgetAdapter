package de.welt.widgetadapter;

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.then
import org.junit.Test

data class Event(val data: String = "")
data class OtherEvent(val data: String = "")

class EventDispatcherTest {

    val classToTest = EventDispatcher()
    val eventHandler: (Event) -> Unit = mock()
    val secondHandler: (Event) -> Unit = mock()
    val otherEventHandler: (OtherEvent) -> Unit = mock()

    @Test
    fun canListenForEvent() {
        classToTest.subscribe<Event>(eventHandler)

        classToTest.dispatch(Event())

        then(eventHandler).should().invoke(Event())
    }

    @Test
    fun canHaveMultipleListenersForTheSameEventType() {
        classToTest.subscribe<Event>(eventHandler)
        classToTest.subscribe<Event>(secondHandler)

        classToTest.dispatch(Event())

        then(eventHandler).should().invoke(Event())
        then(secondHandler).should().invoke(Event())
    }

    @Test
    fun canRemoveHandlerByTag() {
        classToTest.subscribe<Event>("TAG", eventHandler)
        classToTest.unsubscribe<Event>("TAG")

        classToTest.dispatch(Event())

        then(eventHandler).should(never()).invoke(Event())
    }

    @Test
    fun canRemoveAllHandlersByTag() {
        classToTest.subscribe<Event>("TAG", eventHandler)
        classToTest.subscribe<OtherEvent>("TAG", otherEventHandler)
        classToTest.unsubscribeAllOf("TAG")

        classToTest.dispatch(Event())
        classToTest.dispatch(OtherEvent())

        then(eventHandler).should(never()).invoke(Event())
        then(otherEventHandler).should(never()).invoke(OtherEvent())
    }

    @Test
    fun canRemoveAllHandlers() {
        classToTest.subscribe<Event>("TAG", eventHandler)
        classToTest.subscribe<OtherEvent>("TAG", otherEventHandler)
        classToTest.unsubscribeAll()

        classToTest.dispatch(Event())
        classToTest.dispatch(OtherEvent())

        then(eventHandler).should(never()).invoke(Event())
        then(otherEventHandler).should(never()).invoke(OtherEvent())
    }

    @Test
    fun canRemoveAllUntaggedHandlers() {
        classToTest.subscribe<Event>(eventHandler)
        classToTest.unsubscribeUntagged()

        classToTest.dispatch(Event())

        then(eventHandler).should(never()).invoke(Event())
    }

}