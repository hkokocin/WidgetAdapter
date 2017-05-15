package de.welt.widgetadapter

import android.view.LayoutInflater
import android.view.View
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.then
import com.winterbe.expekt.should
import org.junit.Test

internal class WidgetViewHolderProviderTest {

    val view: View = mock()
    val inflater: LayoutInflater = mock()
    val events = EventDispatcher()

    val widget: Widget<Any> = mock {
        given { it.events }.willReturn(events)
    }

    val holder: WidgetViewHolder<Any> = mock{
        given { it.widget }.willReturn(widget)
    }


    val classToTest = WidgetViewHolderProvider(inflater, { widget })

    @Test
    fun canCreateViewHolder() {
        given(widget.createView(inflater, null)).willReturn(view)

        val holder = classToTest.onCreateViewHolder(null)

        holder.itemView.should.be.equal(view)
    }

    @Test
    fun delegatesEventsFromCreatedWidgets() {
        var hit = false
        given(widget.createView(inflater, null)).willReturn(view)

        classToTest.onCreateViewHolder(null)
        classToTest.events.subscribe<Any> { hit = true }
        events.dispatch(Any())

        hit.should.be.`true`
    }

    @Test
    fun updatesDataOnBind() {
        val data = Any()

        classToTest.onBindViewHolder(holder, data)

        then(widget).should().setData(data)
    }

}