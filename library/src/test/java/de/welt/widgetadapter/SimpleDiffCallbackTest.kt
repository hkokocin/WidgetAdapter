package de.welt.widgetadapter

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.same
import com.nhaarman.mockito_kotlin.then
import com.winterbe.expekt.should
import org.junit.Test

internal class SimpleDiffCallbackTest {

    private val newItem = Any()
    private val oldItem = Any()

    val newItems = listOf(newItem)
    val oldItems = listOf(oldItem)
    val areItemsTheSame: SimpleDiffCallback.(Any, Any) -> Boolean = mock()

    val classToTest = SimpleDiffCallback(newItems, oldItems, areItemsTheSame)

    @Test
    fun canGetOldListSize() {
        classToTest.oldListSize.should.be.equal(1)
    }

    @Test
    fun canGetNewListSize() {
        classToTest.newListSize.should.be.equal(1)
    }

    @Test
    fun doSimpleEqualsCheckToDetermineIfContentsAreTheSame() {
        given(areItemsTheSame.invoke(eq(classToTest), any(), any())).willReturn(true)

        classToTest.areItemsTheSame(0, 0).should.be.equal(true)
    }

    @Test
    fun useCallbackToDetermineIfItemsAreTheSame() {
        given(areItemsTheSame.invoke(eq(classToTest), any(), any())).willReturn(true)

        classToTest.areItemsTheSame(0, 0)

        then(areItemsTheSame).should().invoke(same(classToTest)!!, same(oldItem)!!, same(newItem)!!)
    }
}

