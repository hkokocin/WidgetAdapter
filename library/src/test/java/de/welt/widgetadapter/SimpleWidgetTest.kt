package de.welt.widgetadapter

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.hkokocin.androidkit.AndroidKit
import com.github.hkokocin.androidkit.content.getColorInt
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.mock
import com.winterbe.expekt.should
import org.junit.Test


class SimpleWidgetTest {
    val events: EventDispatcher = mock()
    val resources: Resources = mock()
    val container: ViewGroup = mock()
    val view: View = mock()

    val contentView: View = mock {
        given { it.resources }.willReturn(resources)
    }

    val inflater: LayoutInflater = mock {
        given { it.inflate(eq(1), eq(container), any()) }.willReturn(contentView)
    }


    val classToTest = SimpleTestWidget()

    inner class SimpleTestWidget : SimpleWidget<String>(1) {
        var onViewCreatedCalled = false

        override val events: EventDispatcher = this@SimpleWidgetTest.events

        private val view: View by viewId(2)
        private val resource: String by resourceId(3)
        private val color by colorResource(4)
        private val dimension by dimensionInPixels(5)

        override fun setData(data: String) {}

        override fun onViewCreated(view: View) {
            onViewCreatedCalled = viewCreated
        }

        fun initView() = view
        fun initResource() = resource
        fun initColor() = color
        fun initDimension() = dimension
    }

    @Test
    fun canInflateLayout() {
        val view = classToTest.createView(inflater, container)

        view.should.be.equal(contentView)
    }

    @Test
    fun canCheckIfViewWasCreated() {
        classToTest.createView(inflater, container)

        classToTest.viewCreated.should.be.`true`
    }

    @Test
    fun callsOnViewCreatedAfterInflation() {
        classToTest.createView(inflater, container)

        classToTest.onViewCreatedCalled.should.be.`true`
    }

    @Test
    fun canRetrieveViewById() {
        classToTest.createView(inflater, container)
        given(contentView.findViewById(2)).willReturn(view)

        val result = classToTest.initView()

        result.should.be.equal(view)
    }

    @Test
    fun canRetrieveColorResource() {
        classToTest.createView(inflater, container)
        given(resources.getColorInt(4, null)).willReturn(123)
CharS
        val color = classToTest.initColor()

        color.should.be.equal(123)
    }

    @Test
    fun canRetrieveDimension() {
        classToTest.createView(inflater, container)
        given(resources.getDimensionPixelSize(5)).willReturn(123)

        val dimension = classToTest.initDimension()

        dimension.should.be.equal(123)
    }

    @Test
    fun canRetrieveResources() {
        given(resources.getString(any())).willReturn("")
        classToTest.createView(inflater, container)
        val kit = mock<AndroidKit>()
        AndroidKit.instance = kit
        given(kit.getResource(resources, 3, String::class)).willReturn("resource")

        val color = classToTest.initResource()

        color.should.be.equal("resource")
    }

}